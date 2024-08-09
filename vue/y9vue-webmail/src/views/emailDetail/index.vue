<!--
 * @Author: error: git config user.name && git config user.email & please set dead value or install git
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2022-12-29 21:01:23
 * @Description: 邮件详情
-->

<template>
    <div style="height: 100%; overflow-y: auto; overflow-x: hidden; scrollbar-width: none">
        <el-row>
            <el-col>
                <el-button class="global-btn-third" @click="returnEmailList" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-arrow-left-line"></i>
                    <span>&nbsp;{{ $t('返回') }}</span>
                </el-button>
                <el-button class="global-btn-third" @click="reply" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-reply-line"></i>
                    <span>&nbsp;{{ $t('回复') }}</span>
                </el-button>
                <el-button class="global-btn-third" @click="replyAll" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-reply-all-line"></i>
                    <span>&nbsp;{{ $t('全部回复') }}</span>
                </el-button>
                <el-button class="global-btn-third" @click="forwardEmail" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-send-plane-line"></i>
                    <span>&nbsp;{{ $t('转发') }}</span>
                </el-button>
                <!--                <el-button v-if="(flag==-2)" class="global-btn-third" @click="withDraw">
                                    <i class="ri-arrow-go-back-line"></i>
                                    <span>&nbsp;{{ $t("撤回") }}</span>
                                </el-button>-->
                <el-button class="global-btn-third" @click="deleteEM" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-delete-bin-line"></i>
                    <span>&nbsp;{{ $t('删除') }}</span>
                </el-button>
                <el-button class="global-btn-third" @click="completelyDelete" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-delete-bin-line"></i>
                    <span>&nbsp;{{ $t('彻底删除') }}</span>
                </el-button>
                <el-button class="global-btn-third" @click="exportEmail" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-mail-download-line"></i>
                    <span>&nbsp;{{ $t('导出') }}</span>
                </el-button>
                <!--                <el-button class="global-btn-third">
                                    <i class="ri-more-fill"></i>
                                    <span>&nbsp;{{ $t("更多") }}</span>
                                </el-button>-->
                <el-button class="global-btn-third tag-right" @click="nextEmail" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-arrow-right-s-line"></i>
                </el-button>
                <el-button class="global-btn-third tag-right" @click="previousEmail" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-arrow-left-s-line"></i>
                </el-button>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24" class="col-value">
                <el-card class="email-subject-top">
                    <div class="main-detail-content">
                        <div class="new-mailmess-subject">
                            {{ emailValue.email.subject }}
                        </div>
                        <div class="new-mailmess">
                            <div>
                                <span class="contact-name-label"> 发件人 </span>
                            </div>
                            <div>
                                <span class="contact-name-label-f contact-capsule">{{ emailValue.email.from }}</span>
                                <span class="contact-name-suffix"></span>
                            </div>
                        </div>
                        <div class="new-mailmess">
                            <span class="contact-name-label"> 收件人</span>
                            <div>
                                <span class="contact-name-label-s contact-capsule">{{ receivers }}</span>
                                <span class="contact-name-suffix"></span>
                            </div>
                        </div>
                        <div class="new-mailmess" v-if="emailValue.email.ccEmailAddressList != ''">
                            <span class="contact-name-label"> 抄送</span>
                            <div>
                                <span class="contact-name-label-s contact-capsule">{{ ccReceivers }}</span>
                                <span class="contact-name-suffix"></span>
                            </div>
                        </div>
                        <div class="new-mailmess">
                            <span class="mailmess-time-label">时间</span>
                            <span>{{ emailValue.email.sendTime }}</span>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <el-card>
                    <div class="mail-content">
                        <div v-html="emailValue.email.richText"></div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <el-card class="email-subject-bottom">
                    <div class="attachment-body">
                        <div class="attachment-explain">
                            <span v-if="emailValue.email.emailAttachmentDTOList?.length">
                                全部 {{ emailValue.email.emailAttachmentDTOList?.length }} 个附件
                                <span style="color: var(--el-color-primary); cursor: pointer">
                                    <a @click="batchDownload">
                                        <i class="ri-download-2-line" style="vertical-align: middle"></i
                                        ><span style="vertical-align: middle">批量下载</span>
                                    </a>
                                </span>
                            </span>
                        </div>
                        <span v-for="attachment in emailValue.email.emailAttachmentDTOList">
                            <div
                                :title="attachment.fileName"
                                class="attachment-box"
                                @click="download(attachment.fileName)"
                            >
                                <div class="attachment-left-content">
                                    <img
                                        v-if="attachment.fileExt == 'pdf'"
                                        src="@/assets/images/xiangqing/file-pdf-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'ppt'"
                                        src="@/assets/images/xiangqing/file-ppt-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'doc' || attachment.fileExt == 'docx'"
                                        src="@/assets/images/xiangqing/file-word-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'xlsx' || attachment.fileExt == 'xls'"
                                        src="@/assets/images/xiangqing/file-excel-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'txt'"
                                        src="@/assets/images/xiangqing/file-text-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'jpg' || attachment.fileExt == 'png'"
                                        src="@/assets/images/xiangqing/image-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'zip' || attachment.fileExt == 'rar'"
                                        src="@/assets/images/xiangqing/zip.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <img
                                        v-else
                                        src="@/assets/images/xiangqing/file-unknow-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <div class="attachment-title-box">
                                        <div class="attachment-name">{{ attachment.fileName }}</div>
                                        <div class="attachment-usage">{{ attachment.displaySize }}</div>
                                    </div>
                                </div>
                            </div>
                        </span>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24" class="col-value">
                <el-card class="fast-reply">
                    <el-input
                        v-model="quickReply"
                        :placeholder="'快捷回复给:' + emailValue.email.from"
                        clearable
                        class="fast-reply-input"
                    />
                    <el-button class="global-btn-second tag-right" @click="quickEmail" :size="fontSizeObj.buttonSize"
                               :style="{ fontSize: fontSizeObj.baseFontSize }">
                        <i class="ri-send-plane-2-line"></i>
                        <span>&nbsp;{{ $t('回复') }}</span>
                    </el-button>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>
<script lang="ts" setup>
    import { useRouter } from 'vue-router';
    import { onMounted, ref, watch, defineProps } from 'vue';
    import { quickReplyEmail, emailDetail, withDrawEmail, deleteEmail, deleteForeverEmail} from '@/api/email/index';
    import { useI18n } from 'vue-i18n';
    import { ElMessageBox, ElNotification } from 'element-plus';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo')||{};
    const { t } = useI18n();
    const router = useRouter();
    const quickReply = ref('');
    let receivers = ref('');
    let ccReceivers = ref('');
    const props = defineProps({
        folder: { type: String, required: true },
        uid: { type: Number, required: true }
    });

    // 封装邮件属性
    const emailValue = ref({
        attachmentList: [],
        email: {
            folder: props.folder,
            uid: props.uid,
            previousUid: -1,
            nextUid: -1,
            messageId: '',
            replyMessageId: '',
            forwardMessageId: '',
            from: '',
            sendTime: '',
            subject: '',
            richText: '',
            separate: false,
            text: '',
            toEmailAddressList: [],
            ccEmailAddressList: [],
            bccEmailAddressList: [],
            emailAttachmentDTOList: []
        },
        toEmailOrgUnitList: [],
        ccEmailOrgUnitList: [],
        bccEmailOrgUnitList: [],
        toPersonIds: []
    });

    onMounted(() => {
        getEmailDetail();
    });

    watch(
        () => [props.folder, props.uid],
        async () => {
            emailValue.value.email.folder = props.folder;
            emailValue.value.email.uid = props.uid;
            await getEmailDetail();
        }
    );

    async function previousEmail() {
        if (emailValue.value.email.previousUid && emailValue.value.email.previousUid !== -1) {
            router.push({
                path: '/emailDetailInfo',
                query: { folder: emailValue.value.email.folder, uid: emailValue.value.email.previousUid }
            });
        } else {
            ElNotification({
                title: t('没有更多邮件了'), //result.success ? t('成功') : t('失败'),
                type: 'error', //result.success ? 'success' : 'error',
                offset: 80
            });
        }
    }

    //下一封
    async function nextEmail() {
        if (emailValue.value.email.nextUid && emailValue.value.email.nextUid !== -1) {
            router.push({
                path: '/emailDetailInfo',
                query: { folder: emailValue.value.email.folder, uid: emailValue.value.email.nextUid }
            });
            // router.push({path: `/email/${emailValue.value.email.folder}/${emailValue.value.email.nextUid}`});
        } else {
            ElNotification({
                title: t('没有更多邮件了'), //result.success ? t('成功') : t('失败'),
                type: 'error', //result.success ? 'success' : 'error',
                offset: 80
            });
        }
    }

    //转发
    function forwardEmail() {
        let folder = emailValue.value.email.folder;
        let uid = emailValue.value.email.uid;
        router.push({ path: '/writing/forward', query: { folder: folder, uid: uid } });
    }

    //撤回
    async function withDraw() {
        let emailId = emailValue.value.email.folder;
        let result = await withDrawEmail({ emailId: emailId });
        if (result.success) {
            ElNotification({
                title: t('成功'), //result.success ? t('成功') : t('失败'),
                message: result.msg, //result.msg,
                type: 'success', //result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            router.push({ path: '/sent' });
        } else {
            ElNotification({
                title: t('失败'), //result.success ? t('成功') : t('失败'),
                message: result.msg, //result.msg,
                type: 'error', //result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        }
    }

    //删除
    function deleteEM(e) {
        ElMessageBox.confirm(`${t('是否删除邮件')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                let ids = [emailValue.value.email.uid];
                //调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result = await deleteEmail(emailValue.value.email.folder, ids.join(','));
                if (result.success) {
                    ElNotification({
                        title: t('成功'), //result.success ? t('成功') : t('失败'),
                        message: result.msg, //result.msg,
                        type: 'success', //result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                }
                router.go(-1);
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    //彻底删除
    function completelyDelete(e) {
        ElMessageBox.confirm(`${t('是否彻底删除邮件')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                let ids = [emailValue.value.email.uid];
                //调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result = await deleteForeverEmail(emailValue.value.email.folder, ids.join(','));
                if (result.success) {
                    ElNotification({
                        title: t('成功'), //result.success ? t('成功') : t('失败'),
                        message: result.msg, //result.msg,
                        type: 'success', //result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                }
                router.go(-1);
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    function exportEmail() {
        let url =
            import.meta.env.VUE_APP_EMAIL_URL +
            'api/standard/email/exportEml?uid=' +
            emailValue.value.email.uid +
            '&folder=' +
            emailValue.value.email.folder +
            '&access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        // window.open(url, '_blank');
        var link = document.createElement('a');
        link.href = url;
        link.click();
    }

    //快捷回复
    async function quickEmail() {
        if (quickReply.value == '') {
            ElMessageBox({ title: '提示', message: '请先输入快捷回复内容' });
            return;
        }
        let folder = emailValue.value.email.folder;
        let uid = emailValue.value.email.uid;
        let quickResult = await quickReplyEmail(folder, uid, quickReply.value);
        if (quickResult.success) {
            ElNotification({
                title: t('成功'), //result.success ? t('成功') : t('失败'),
                message: quickResult.msg, //result.msg,
                type: 'success', //result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            quickReply.value = '';
            // router.go(-1);
        } else {
            ElNotification({
                title: t('失败'), //result.success ? t('成功') : t('失败'),
                message: '发送失败', //result.msg,
                type: 'error', //result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        }
    }

    //回复
    async function reply() {
        let folder = emailValue.value.email.folder;
        let uid = emailValue.value.email.uid;
        router.push({ path: '/writing/reply', query: { folder: folder, uid: uid } });
    }

    //全部回复
    async function replyAll() {
        let folder = emailValue.value.email.folder;
        let uid = emailValue.value.email.uid;
        router.push({ path: '/writing/replyAll', query: { folder: folder, uid: uid } });
    }

    //下载单个附件
    function download(fileName) {
        let downloadUrl =
            import.meta.env.VUE_APP_EMAIL_URL +
            'api/standard/emailAttachment/download?messageId=' +
            encodeURIComponent(emailValue.value.email.messageId) +
            '&folder=' +
            emailValue.value.email.folder +
            '&fileName=' +
            fileName +
            '&access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        // window.open(downloadUrl, '_blank');
        var link = document.createElement('a');
        link.href = downloadUrl;
        link.click();
    }

    //批量下载附件
    function batchDownload() {
        let downloadUrl =
            import.meta.env.VUE_APP_EMAIL_URL +
            'api/standard/emailAttachment/batchDownload?messageId=' +
            encodeURIComponent(emailValue.value.email.messageId) +
            '&folder=' +
            emailValue.value.email.folder +
            '&access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        // window.open(downloadUrl, '_blank');
        var link = document.createElement('a');
        link.href = downloadUrl;
        link.click();
    }

    //返回按钮,返回列表
    function returnEmailList() {
        router.go(-1);
    }

    async function getEmailDetail() {
        let folder = emailValue.value.email.folder;
        let uid = emailValue.value.email.uid;
        let emailData = await emailDetail(folder, uid);
        emailValue.value.email = emailData.data;

        emailValue.value.email.richText = emailData.data.richText;
        for (let i = 0; i < emailValue.value.email.toEmailAddressList.length; i++) {
            receivers.value = emailValue.value.email.toEmailAddressList[i] + ',';
        }
        if (emailData.data.ccEmailAddressList.length > 0) {
            for (let i = 0; i < emailValue.value.email.ccEmailAddressList.length; i++) {
                ccReceivers.value = emailValue.value.email.ccEmailAddressList[i] + ',';
            }
            ccReceivers.value = ccReceivers.value.substring(0, ccReceivers.value.length - 1);
        }
        receivers.value = receivers.value.substring(0, receivers.value.length - 1);
    }
</script>
<style lang="scss" scoped>
    :deep(.el-card) {
        border-radius: 0px;
    }

    :deep(.el-card__body) {
        padding: 10px;
    }

    .col-value {
        display: flex;
        width: 100%;
        margin-top: 20px;

        //快捷回复
        .fast-reply {
            width: 100%;
            height: 57px;
            border-radius: 5px;
            margin-bottom: 3px;
            box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
            //快捷回复-输入框
            :deep(.el-card__body) {
                padding: 0px;
                height: 100%;
            }

            //快捷回复-发送按钮样式
            :deep(.global-btn-second) {
                height: 100%;
                border: 0 solid transparent;
                font-size: 17px;
                flex-wrap: wrap;
                overflow: hidden;
                padding: 0px 20px 0px 20px;
                margin-left: 0px;
            }

            //快捷回复-输入框
            :deep(.fast-reply-input) {
                width: calc(100% - 105px - 1vw);
                height: 30px;
                line-height: 30px;
                font-size: 17px;
                margin-top: 11px;
                margin-left: 15px;
                border-radius: 15px;
                border: 0;
                text-indent: 18px;
                float: left;
            }
        }

        //邮件详情(头部)
        .email-subject-top {
            width: 100%;
            border-bottom: 2px solid;
            background-color: #ececf1;
            border-bottom-color: var(--el-color-primary);
            border-radius: 5px 5px 0 0;
        }
    }

    //邮件详情(底部)
    .email-subject-bottom {
        border-radius: 0 0 5px 5px;
    }

    //按钮靠右
    .tag-right {
        float: right;
    }

    :deep(.el-col) {
        padding: 0 !important;
    }

    // el-card 阴影效果
    :deep(.el-card.is-always-shadow) {
        box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
    }

    :deep(.el-col-8) {
        .card-value:nth-child(1) {
            margin: 0 30px;
        }
    }

    :deep(.el-input__wrapper) {
        box-shadow: none;
    }

    @media screen and (max-width: 1290px) {
        :deep(.op-calendar-pc-table td a) {
            width: 33px;
        }
        :deep(.op-calendar-pc-backtoday) {
            font-size: 12px;
        }
        :deep(.op-calendar-pc-holiday-box) {
            margin-right: 2px;

            .ant-select-selector {
                width: 80px !important;
            }

            .ant-select {
                width: 80px !important;
            }
        }
        :deep(.op-calendar-pc-year-box) {
            margin-right: 2px;
            width: 80px;

            .ant-select-selector {
                width: 80px !important;
            }

            .ant-select {
                width: 80px !important;
            }
        }
    }

    @media screen and (max-width: 1460px) and (min-width: 1290px) {
        :deep(.op-calendar-pc-table td a) {
            width: 35px;
        }
        :deep(.op-calendar-pc-holiday-box) {
            margin-right: 10px;

            .ant-select-selector {
                width: 85px !important;
            }

            .ant-select {
                width: 85px !important;
            }
        }
        :deep(.op-calendar-pc-year-box) {
            margin-right: 10px;

            .ant-select-selector {
                width: 85px !important;
            }

            .ant-select {
                width: 85px !important;
            }
        }
    }

    @media screen and (max-width: 1800px) and (min-width: 1460px) {
        :deep(.op-calendar-pc-table td a) {
            width: 42px;
        }
        :deep(.op-calendar-pc-holiday-box) {
            margin-right: 15px;
        }
        :deep(.op-calendar-pc-year-box) {
            margin-right: 15px;
        }
    }

    @media screen and (max-width: 1900px) and (min-width: 1800px) {
        :deep(.op-calendar-pc-table td a) {
            width: 55px;
        }
    }

    @media screen and (min-width: 1900px) {
        :deep(.op-calendar-pc-table td a) {
            width: 58px;
        }
    }

    //邮件主体样式
    .main-detail-content {
        padding: 16px 24px 0px;
    }

    .new-mailmess-subject {
        padding: 0px 10px;
        cursor: default;
        font:
            bold 24px 'lucida Grande',
            Verdana,
            'Microsoft YaHei';
    }

    .new-mailmess {
        /*border-bottom: #acb6d8 solid 1px;*/
        display: flex;
        margin-top: 5px;
        margin-bottom: 5px;
        line-height: 26px;
    }

    .contact-name-label,
    .mailmess-time-label {
        padding: 0px 10px;
        display: inline-block;
        opacity: 0.6;
        line-height: 24px;
        cursor: default;
        flex-shrink: 0;
    }

    .contact-capsule:hover,
    .contact-capsule:hover {
        background: var(--el-color-primary-light-9);
        border: 1px solid;
        border-color: var(--el-color-primary-light-4);
        cursor: pointer;
        border-radius: 4px;
    }

    .contact-name-suffix {
        opacity: 0.4;
        cursor: default;
    }

    .contact-name-label-f {
        color: var(--el-color-primary);
        display: inline-block;
        line-height: 24px;
        cursor: default;
        flex-shrink: 0;
        border: 1px solid #00000000;
    }

    .contact-name-label-s {
        color: var(--el-color-primary);
        display: inline-block;
        line-height: 24px;
        cursor: default;
        flex-shrink: 0;
        border: 1px solid #00000000;
    }

    .mail-content {
        //padding: 0px 10px;
        padding: 0px 35px;
        font-size: 16px;
        margin-top: 12px;
        //border-top:1px solid #ededed;
        margin-bottom: 12px;
        //border-bottom:1px solid #ededed;
        word-break: break-word;
        word-wrap: break-word;
        flex: 1;
    }

    .mail-content img {
        max-width: 100%;
    }

    .attachment-body {
        //padding: 0px 10px;
        padding: 10px 35px;
        //margin-top:12px;
    }

    .attachment-explain {
        margin-bottom: 10px;
    }

    .attachment-box {
        position: relative;
        width: 198px;
        min-width: 198px;
        max-width: 386px;
        height: 60px;
        display: flex;
        background: #ededed8c;
        border: 1px solid #ededed;
        border-radius: 4px;
        margin-right: 8px;
        margin-bottom: 8px;
        box-sizing: border-box;
        width: 364px;
        margin-right: 8px;
        display: inline-block;
        opacity: 0.8;
        cursor: pointer;
    }

    .attachment-box:hover {
        background: #ededed;
        opacity: 1;
    }

    .attachment-left-content {
        display: flex;
        flex: 1;
        padding: 0 8px;
        align-items: center;
        height: 100%;
    }

    .attachment-icon {
        width: 48px;
        height: 48px;
    }

    .attachment-title-box {
        margin-left: 8px;
        display: flex;
        flex: 1;
        width: 0;
        flex-direction: column;
    }

    .attachment-name {
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        line-height: 20px;
        font-size: 15px;
        color: #030303;
        opacity: 0.9;
    }

    .attachment-usage {
        font-size: 13px;
        color: #000;
        opacity: 0.6;
    }
</style>
