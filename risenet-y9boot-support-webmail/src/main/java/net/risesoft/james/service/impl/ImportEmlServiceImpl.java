package net.risesoft.james.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.dom.field.ContentDispositionField;
import org.apache.james.mime4j.dom.field.ContentTypeField;
import org.apache.james.mime4j.dom.field.FieldName;
import org.apache.james.mime4j.field.ContentTypeFieldLenientImpl;
import org.apache.james.mime4j.message.MultipartImpl;
import org.apache.james.mime4j.stream.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.net.MediaType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.Y9IdGenerator;
import net.risesoft.james.entity.ImportEml;
import net.risesoft.james.repository.ImportEmlRepository;
import net.risesoft.james.service.ImportEmlAttchMentsService;
import net.risesoft.james.service.ImportEmlService;
import net.risesoft.pojo.EmlResult;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;

@Service(value = "importEmlService")
@Slf4j
@RequiredArgsConstructor
public class ImportEmlServiceImpl implements ImportEmlService {

    private final ImportEmlRepository importEmlRepository;

    private final ImportEmlAttchMentsService importEmlAttchMentsService;

    /**
     * 转换邮件联系人至list集合
     *
     * @param addressList 邮件联系人
     * @return list集合
     */
    private List<Pair<String, String>> addressToList(AddressList addressList) {
        List<Pair<String, String>> list = Lists.newArrayList();
        if (addressList == null) {
            return list;
        }
        for (Address address : addressList) {
            Mailbox mailbox = (Mailbox)address;
            String name = mailbox.getName();
            if (StringUtils.isBlank(name)) {
                name = mailbox.getLocalPart();
            }
            list.add(Pair.of(name, mailbox.getAddress()));
        }
        return list;
    }

    private String addressToString(AddressList addressList) {
        StringBuilder sb = new StringBuilder();
        if (addressList == null || addressList.isEmpty()) {
            return sb.toString();
        }
        for (Address address : addressList) {
            Mailbox mailbox = (Mailbox)address;
            String name = mailbox.getName();
            if (StringUtils.isBlank(name)) {
                name = mailbox.getLocalPart();
            }
            sb.append(name + "<" + mailbox.getAddress() + ">,");
        }
        String addressStr = sb.toString();
        return addressStr.substring(0, addressStr.length() - 1);
    }

    /**
     * 转换邮件联系人至String
     *
     * @param addressList 邮件联系人
     * @return String数据
     */
    private String addressToString(MailboxList addressList) {
        if (addressList == null) {
            return StringUtils.EMPTY;
        }
        for (Address address : addressList) {
            return address.toString();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void importEmailByEml(Message message) throws IOException {
        EmlResult emlDTO = new EmlResult();
        emlDTO.setMessage(message);
        emlDTO.setMessageId(message.getMessageId());
        emlDTO.setSubject(message.getSubject());
        emlDTO.setFrom(addressToString(message.getFrom()));
        emlDTO.setTo(addressToList(message.getTo()));
        emlDTO.setCc(addressToList(message.getCc()));
        emlDTO.setBcc(addressToList(message.getBcc()));
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        emlDTO.setDateTime(sdf.format(message.getDate()));
        if (message.getBody() instanceof TextBody) {
            TextBody textBody = (TextBody)message.getBody();
            emlDTO = outputContentInText(message.getHeader(), textBody, emlDTO);
        } else {
            MultipartImpl body = (MultipartImpl)message.getBody();
            List<Entity> bodyParts = body.getBodyParts();
            // 邮件附件和内容
            emlDTO = outputContentAndAttachments(bodyParts, emlDTO);
        }

        Optional<ImportEml> emlOptional = importEmlRepository.findByMessageId(emlDTO.getMessageId());
        if (emlOptional.isEmpty()) {
            ImportEml eml = new ImportEml();
            eml.setId(Y9IdGenerator.genId());
            eml.setPersonId(Y9LoginUserHolder.getPersonId());
            eml.setMessageId(emlDTO.getMessageId());
            eml.setSubject(emlDTO.getSubject());
            eml.setFrom(emlDTO.getFrom());
            eml.setTo(addressToString(message.getTo()));
            eml.setCc(addressToString(message.getCc()));
            eml.setBcc(addressToString(message.getBcc()));
            eml.setDateTime(emlDTO.getDateTime());
            eml.setTextContent(emlDTO.getTextContent());
            eml.setHtmlContent(emlDTO.getHtmlContent());
            eml.setExistAttchMent(!emlDTO.getAttachments().isEmpty());
            eml = importEmlRepository.save(eml);
            if (!emlDTO.getAttachments().isEmpty()) {
                List<MutableTriple<String, Long, InputStream>> attachments = emlDTO.getAttachments();
                for (MutableTriple<String, Long, InputStream> attachment : attachments) {
                    importEmlAttchMentsService.saveAttchMents(eml.getId(), attachment.getLeft(), attachment.getMiddle(),
                        attachment.getRight());
                }
            }
        }
    }

    @Override
    public ImportEml getById(String id) {
        return importEmlRepository.findById(id).orElse(null);
    }

    @Override
    public Page<ImportEml> pageByPersonId(String personId, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "dateTime"));
        return importEmlRepository.findByPersonId(personId, pageable);
    }

    /**
     * 递归处理邮件附件（附件区域附件、内容中的base64图片附件）、邮件内容（纯文本、html富文本）
     *
     * @param bodyParts 邮件内容体
     * @param entry 数据对象
     * @throws IOException 异常处理
     */
    private EmlResult outputContentAndAttachments(List<Entity> bodyParts, EmlResult entry) throws IOException {
        for (Entity bodyPart : bodyParts) {
            Body bodyContent = bodyPart.getBody();
            String dispositionType = bodyPart.getDispositionType();
            if (ContentDispositionField.DISPOSITION_TYPE_ATTACHMENT.equals(dispositionType)) {
                // 正常的附件文件
                BinaryBody binaryBody = (BinaryBody)bodyContent;
                entry.getAttachments()
                    .add(MutableTriple.of(bodyPart.getFilename(), binaryBody.size(), binaryBody.getInputStream()));
                continue;
            }
            if (bodyContent instanceof TextBody) {
                // 纯文本内容
                TextBody textBody = (TextBody)bodyContent;
                outputContentInText(bodyPart.getHeader(), textBody, entry);
            } else if (bodyContent instanceof Multipart) {
                MultipartImpl multipart = (MultipartImpl)bodyContent;
                outputContentAndAttachments(multipart.getBodyParts(), entry);
            } else if (bodyContent instanceof BinaryBody) {
                BinaryBody binaryBody = (BinaryBody)bodyContent;
                outputContentInAttachment(bodyPart.getHeader(), binaryBody, entry);
            } else {
                LOGGER.error("【是否还存在未覆盖到的其它内容类型场景】？");
            }
        }
        return entry;
    }

    /**
     * 处理内容中的图片附件
     *
     * @param header 附件头信息对象
     * @param binaryBody 附件对象
     * @param entry 解析数据对象
     */
    private EmlResult outputContentInAttachment(Header header, BinaryBody binaryBody, EmlResult entry)
        throws IOException {
        Field contentIdField = header.getField(FieldName.CONTENT_ID);
        Field typeField = header.getField(FieldName.CONTENT_TYPE);
        if (typeField instanceof ContentTypeField) {
            ContentTypeField contentTypeField = (ContentTypeField)typeField;
            if (contentTypeField.getMediaType().startsWith(MediaType.ANY_IMAGE_TYPE.type())) {
                try (InputStream inputStream = binaryBody.getInputStream()) {
                    String base64 = Base64.getEncoder().encodeToString(IOUtils.toByteArray(inputStream));
                    String cid = StringUtils.substringBetween(contentIdField.getBody(), "<", ">");
                    String content = StringUtils.replace(entry.getHtmlContent(), "cid:" + cid,
                        "data:" + contentTypeField.getMimeType() + ";base64," + base64);
                    entry.setHtmlContent(content);
                }
            }
        }
        return entry;
    }

    /**
     * 获取邮件内容
     *
     * @param header 头信息对象
     * @param body 对象
     * @param entry 解析数据对象
     * @return EmlResult
     * @throws IOException
     */
    private EmlResult outputContentInText(Header header, TextBody body, EmlResult entry) throws IOException {
        ContentTypeFieldLenientImpl contentType =
            (ContentTypeFieldLenientImpl)header.getField(HttpHeaders.CONTENT_TYPE);
        String mimeType = contentType.getMimeType();
        // 可动态获取内容的编码，按编码转换
        if (MediaType.PLAIN_TEXT_UTF_8.toString().startsWith(mimeType)) {
            entry.setTextContent(IOUtils.toString(body.getReader()));
        }
        if (MediaType.HTML_UTF_8.toString().startsWith(mimeType)) {
            entry.setHtmlContent(IOUtils.toString(body.getReader()));
        }
        return entry;
    }
}
