<!--
 * @Author: 
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-23 17:15:27
 * @Description: 邮件详情
-->
<template>
    <div class="mainmail">
        <el-row>
            <el-col>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="returnEmailList"
                >
                    <i class="ri-arrow-left-line"></i>
                    <span>&nbsp;{{ $t('返回') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="reply"
                >
                    <i class="ri-reply-line"></i>
                    <span>&nbsp;{{ $t('回复') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="replyAll"
                >
                    <i class="ri-reply-all-line"></i>
                    <span>&nbsp;{{ $t('全部回复') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="forwardEmail"
                >
                    <i class="ri-send-plane-line"></i>
                    <span>&nbsp;{{ $t('转发') }}</span>
                </el-button>
                <!--                <el-button v-if="(flag==-2)" class="global-btn-third" @click="withDraw">
                                    <i class="ri-arrow-go-back-line"></i>
                                    <span>&nbsp;{{ $t("撤回") }}</span>
                                </el-button>-->
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="deleteEM"
                >
                    <i class="ri-delete-bin-line"></i>
                    <span>&nbsp;{{ $t('删除') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="completelyDelete"
                >
                    <i class="ri-delete-bin-line"></i>
                    <span>&nbsp;{{ $t('彻底删除') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="exportEmail"
                >
                    <i class="ri-mail-download-line"></i>
                    <span>&nbsp;{{ $t('导出') }}</span>
                </el-button>
                <!--                <el-button class="global-btn-third">
                                    <i class="ri-more-fill"></i>
                                    <span>&nbsp;{{ $t("更多") }}</span>
                                </el-button>-->
                <el-button
                    v-if="email.nextUid == null"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third tag-right"
                    disabled
                    @click="nextEmail"
                >
                    <i class="ri-arrow-right-s-line"></i>
                </el-button>
                <el-button
                    v-else
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third tag-right"
                    @click="nextEmail"
                >
                    <i class="ri-arrow-right-s-line"></i>
                </el-button>
                <el-button
                    v-if="email.previousUid == null"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third tag-right"
                    disabled
                    @click="previousEmail"
                >
                    <i class="ri-arrow-left-s-line"></i>
                </el-button>
                <el-button
                    v-else
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third tag-right"
                    @click="previousEmail"
                >
                    <i class="ri-arrow-left-s-line"></i>
                </el-button>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24" class="col-value">
                <div class="email-subject-top">
                    <div class="main-detail-content">
                        <div class="new-mailmess-subject">
                            {{ email.subject }}
                        </div>
                        <div class="new-mailmess">
                            <div>
                                <span class="contact-name-label"> 发件人：</span>
                            </div>
                            <div>
                                <span class="contact-name-label-f contact-capsule">{{ email.from }}</span>
                                <span class="contact-name-suffix"></span>
                            </div>
                        </div>
                        <div class="new-mailmess">
                            <span class="contact-name-label"> 收件人：</span>
                            <div class="contact-context">
                                <div v-for="(to, i) in email.toEmailAddressList" class="contact-div">
                                    <span class="contact-name-label-s contact-capsule">
                                        {{ to }}
                                    </span>
                                    <span class="contact-name-suffix">
                                        <template v-if="i != email.toEmailAddressList.length - 1"> ; </template>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div v-if="email.ccEmailAddressList?.length" class="new-mailmess">
                            <span class="contact-name-label"> 抄&nbsp;送：</span>
                            <div class="contact-context">
                                <div v-for="(cc, j) in email.ccEmailAddressList" class="contact-div">
                                    <span class="contact-name-label-s contact-capsule">
                                        {{ cc }}
                                    </span>
                                    <span class="contact-name-suffix">
                                        <template v-if="j != email.ccEmailAddressList.length - 1">;</template>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="new-mailmess">
                            <span class="mailmess-time-label">时&nbsp;间：</span>
                            <span>{{ email.sendTime }}</span>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="24">
                <div :style="bodyStyle" class="mail-content">
                    <div v-html="email.richText"></div>
                </div>
            </el-col>
            <el-col v-if="email.emailAttachmentDTOList?.length" :span="24" style="padding: 2px">
                <div class="email-attachment">
                    <div class="attachment-top attachment-explain">
                        <span class="title"> <i class="ri-attachment-2"></i>附件</span>({{
                            email.emailAttachmentDTOList?.length
                        }}
                        个)
                        <span style="color: var(--el-color-primary); cursor: pointer">
                            <a @click="batchDownload">
                                <i class="ri-download-2-line" style="vertical-align: middle"></i
                                ><span style="vertical-align: middle">批量下载</span>
                            </a>
                        </span>
                    </div>
                    <div class="attachment-body">
                        <span v-for="attachment in email.emailAttachmentDTOList">
                            <div
                                :title="attachment.fileName"
                                class="attachment-box"
                                @click="download(attachment.fileName)"
                            >
                                <div class="attachment-left-content">
                                    <img
                                        v-if="attachment.fileExt == 'pdf'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/file-pdf-line.png"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'ppt'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/file-ppt-line.png"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'doc' || attachment.fileExt == 'docx'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/file-word-line.png"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'xlsx' || attachment.fileExt == 'xls'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/file-excel-line.png"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'txt'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/file-text-line.png"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'jpg' || attachment.fileExt == 'png'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/image-line.png"
                                    />
                                    <img
                                        v-else-if="attachment.fileExt == 'zip' || attachment.fileExt == 'rar'"
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/zip.png"
                                    />
                                    <img
                                        v-else
                                        alt=""
                                        class="attachment-icon"
                                        src="@/assets/images/xiangqing/file-unknow-line.png"
                                    />
                                    <div class="attachment-title-box">
                                        <div class="attachment-name">{{ attachment.fileName }}</div>
                                        <div class="attachment-usage">{{ attachment.displaySize }}</div>
                                    </div>
                                </div>
                            </div>
                        </span>
                    </div>
                </div>
            </el-col>
            <el-col :span="24" class="col-value">
                <el-card class="fast-reply">
                    <el-input
                        v-model="quickReply"
                        :placeholder="'快捷回复给:' + email.from"
                        class="fast-reply-input"
                        clearable
                    />
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second tag-right"
                        @click="quickEmail"
                    >
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
    import { inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { deleteEmail, deleteForeverEmail, emailDetail, quickReplyEmail, withDrawEmail } from '@/api/email/index';
    import { useI18n } from 'vue-i18n';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const { t } = useI18n();
    const router = useRouter();
    const quickReply = ref('');
    const props = defineProps({
        folder: { type: String, required: true },
        uid: { type: Number, required: true }
    });

    // 封装邮件属性
    const data = reactive({
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
            emailAttachmentDTOList: [] as any
        },
        toEmailOrgUnitList: [],
        ccEmailOrgUnitList: [],
        bccEmailOrgUnitList: [],
        toPersonIds: [],
        bodyStyle: {
            borderBottomLeftRadius: 'none',
            borderBottomRightRadius: 'none'
        }
    });
    let { attachmentList, email, toEmailOrgUnitList, ccEmailOrgUnitList, bccEmailOrgUnitList, toPersonIds, bodyStyle } =
        toRefs(data);

    onMounted(() => {
        getEmailDetail();
    });

    watch(
        () => [props.folder, props.uid],
        async () => {
            email.value.folder = props.folder;
            email.value.uid = props.uid;
            await getEmailDetail();
        }
    );

    async function previousEmail() {
        if (email.value.previousUid && email.value.previousUid !== -1) {
            router.push({
                path: '/emailDetailInfo',
                query: { folder: email.value.folder, uid: email.value.previousUid }
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
        if (email.value.nextUid && email.value.nextUid !== -1) {
            router.push({
                path: '/emailDetailInfo',
                query: { folder: email.value.folder, uid: email.value.nextUid }
            });
            // router.push({path: `/email/${email.value.folder}/${email.value.nextUid}`});
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
        let folder = email.value.folder;
        let uid = email.value.uid;
        router.push({ path: '/writing/forward', query: { folder: folder, uid: uid } });
    }

    //撤回
    async function withDraw() {
        let emailId = email.value.folder;
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
                let ids = [email.value.uid];
                //调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result = await deleteEmail(email.value.folder, ids.join(','));
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
                let ids = [email.value.uid];
                //调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result = await deleteForeverEmail(email.value.folder, ids.join(','));
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
            email.value.uid +
            '&folder=' +
            email.value.folder +
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
        let folder = email.value.folder;
        let uid = email.value.uid;
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
        let folder = email.value.folder;
        let uid = email.value.uid;
        router.push({ path: '/writing/reply', query: { folder: folder, uid: uid } });
    }

    //全部回复
    async function replyAll() {
        let folder = email.value.folder;
        let uid = email.value.uid;
        router.push({ path: '/writing/replyAll', query: { folder: folder, uid: uid } });
    }

    //下载单个附件
    function download(fileName) {
        let downloadUrl =
            import.meta.env.VUE_APP_EMAIL_URL +
            'api/standard/emailAttachment/download?messageId=' +
            encodeURIComponent(email.value.messageId) +
            '&folder=' +
            email.value.folder +
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
            encodeURIComponent(email.value.messageId) +
            '&folder=' +
            email.value.folder +
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
        let folder = email.value.folder;
        let uid = email.value.uid;
        let emailData = await emailDetail(folder, uid);
        email.value = emailData.data;
        email.value.richText = emailData.data.richText;
        if (email.value.emailAttachmentDTOList?.length) {
            attachmentList.value = email.value.emailAttachmentDTOList;
            bodyStyle.value = {
                borderBottomLeftRadius: '0px',
                borderBottomRightRadius: '0px'
            };
        } else {
            bodyStyle.value = {
                borderBottomLeftRadius: '5px',
                borderBottomRightRadius: '5px'
            };
        }
    }
</script>
<style lang="scss" scoped>
    :deep(.el-card) {
        border-radius: 0px;
    }

    :deep(.el-card__body) {
        padding: 10px;
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
</style>

<style lang="scss" scoped>
    //邮件主体样式
    .mainmail {
        height: 100%;
        overflow-y: auto;
        // overflow-x: auto;
        overflow-x: hidden;
        scrollbar-width: none;
        border-bottom-left-radius: 5px;
        border-bottom-right-radius: 5px;

        .col-value {
            display: flex;
            width: 100%;
            margin-top: 20px;

            //邮件详情(头部)
            .email-subject-top {
                width: 100%;
                border-bottom: 2px solid;
                background-color: #f5f5f5;
                border-bottom-color: var(--el-color-primary);
                border-top-left-radius: 5px;
                border-top-right-radius: 5px;

                .main-detail-content {
                    padding: 16px 20px 0px;

                    .new-mailmess-subject {
                        //padding: 0px 10px;
                        cursor: default;
                        font: bold 24px 'lucida Grande', Verdana, 'Microsoft YaHei';
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
                        padding-right: 10px;
                        display: inline-block;
                        opacity: 0.6;
                        line-height: 24px;
                        cursor: default;
                        flex-shrink: 0;
                    }

                    .spacing {
                        letter-spacing: 15px;
                    }

                    .contact-context {
                        display: inline-block;

                        .contact-div {
                            cursor: pointer;
                            float: left;
                        }
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
                        cursor: pointer;
                        color: #7f7f7f;
                    }

                    .contact-name-label-f {
                        color: var(--el-color-primary);
                        display: inline-block;
                        line-height: 24px;
                        cursor: pointer;
                        flex-shrink: 0;
                        border: 1px solid #00000000;
                    }

                    .contact-name-label-s {
                        color: var(--el-color-primary);
                        display: inline-block;
                        line-height: 24px;
                        cursor: pointer;
                        flex-shrink: 0;
                        border: 1px solid #00000000;
                    }
                }
            }

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
        }

        .mail-content {
            //padding: 0px 10px;
            padding: 12px 20px;
            font-size: 16px;
            //border-top:1px solid #ededed;
            //border-bottom:1px solid #ededed;
            word-break: break-word;
            word-wrap: break-word;
            flex: 1;
            background-color: rgb(255, 255, 255);
            overflow-x: auto;

            img {
                max-width: 100%;
            }
        }

        //附件列表
        .email-attachment {
            background: rgb(255, 255, 255);

            .attachment-top {
                text-align: left;
                padding: 8px 10px;
                background: #f2f2f2;
                margin-left: 2px;
                margin-right: 2px;

                .title {
                    font-weight: 700;
                }
            }

            .attachment-body {
                //padding: 0px 10px;
                padding: 10px 20px;
                //margin-top:12px;
                background-color: rgb(255, 255, 255);
                border-bottom-left-radius: 5px;
                border-bottom-right-radius: 5px;

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

                        flex-direction: column;
                    }

                    .attachment-name {
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        overflow: hidden;
                        line-height: 20px;
                        font-size: 15px;
                        color: #2b2b2b;
                    }

                    .attachment-usage {
                        font-size: 13px;
                        color: #000;
                        opacity: 0.6;
                    }
                }

                .attachment-box:hover {
                    background: #ededed;
                    opacity: 1;
                }
            }
        }
    }
</style>
