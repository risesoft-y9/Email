<template>
    <div id="btnDiv" class="">
        <el-button
            type="primary"
            class="global-btn-main"
            @click="saveOfSend('send')"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-send-plane-fill" style="vertical-align: middle; font-size: large; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('发送') }}</span>
        </el-button>
        <el-button
            class="global-btn-third"
            @click="saveOfSend('save')"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-save-line" style="vertical-align: middle; font-size: large; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('存草稿') }}</span>
        </el-button>
        <el-button
            type="primary"
            class="global-btn-third"
            @click="cancel()"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-close-circle-line" style="vertical-align: middle; font-size: large; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('关闭') }}</span>
        </el-button>
        <el-button
            v-if="!cmEnable"
            class="global-btn-third"
            @click="toggleCC()"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-add-line" style="font-size: large; vertical-align: middle; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('抄送密送') }}</span></el-button
        >
        <el-button
            v-if="cmEnable"
            class="global-btn-third"
            @click="toggleCC()"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-subtract-line" style="font-size: large; vertical-align: middle; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('抄送密送') }}</span></el-button
        >
        <el-button
            v-if="recentContactEnable"
            type="primary"
            plain
            class="lineBtn global-btn-third"
            @click="toLine()"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-menu-fold-line" style="font-size: large"></i>
        </el-button>
        <el-button
            v-if="!recentContactEnable"
            type="primary"
            plain
            class="lineBtn global-btn-third"
            @click="toLine()"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-menu-unfold-line" style="font-size: large"></i>
        </el-button>
    </div>

    <el-row :gutter="20" style="margin-top: 20px; margin-left: 0px; margin-right: 0px">
        <el-col
            :span="recentContactEnable ? 20 : 24"
            style="
                background-color: #fff;
                height: 100%;
                padding-right: 18px;
                padding-left: 18px;
                box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
            "
        >
            <div id="containerDiv" class="">
                <el-form :inline="true" :model="formInline" class="demo-form-inline">
                    <el-form-item class="item-form">
                        <span
                            class="select"
                            title="点击选择收件人"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            style="width: 60px; min-width: 60px; text-align: right; margin-right: 16px"
                            @click="selectPersonDialog('to')"
                            >收件人</span
                        >
                        <el-select
                            v-model="emailValue.email.toEmailAddressList"
                            multiple
                            filterable
                            remote
                            :reserve-keyword="false"
                            allow-create
                            default-first-option
                            :remote-method="searchMethod"
                            :loading="loading"
                            placeholder="请选择收件人"
                            loading-text="加载中"
                            class="box-input"
                            size="large"
                            @remove-tag="removeTag"
                            @change="changeValue"
                        >
                            <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>

                    <el-form-item style="width: 100%">
                        <span
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            style="width: 60px; min-width: 60px; text-align: right; margin-right: 16px"
                        >
                            主题
                        </span>
                        <el-input
                            v-model="emailValue.email.subject"
                            class="box-input"
                            placeholder="请输入主题"
                            size="large"
                        />
                    </el-form-item>

                    <el-form-item v-if="cmEnable" style="width: 100%">
                        <span
                            class="select"
                            style="width: 60px; min-width: 60px; text-align: right; margin-right: 16px"
                            title="点击选择抄送人"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            @click="selectPersonDialog('cc')"
                            >抄送人</span
                        >
                        <el-select
                            v-model="emailValue.email.ccEmailAddressList"
                            multiple
                            filterable
                            remote
                            :reserve-keyword="false"
                            placeholder="请选择抄送人"
                            :remote-method="searchMethod"
                            :loading="loading"
                            size="large"
                            class="box-input"
                            @remove-tag="removeCcEmailOrgUnitListTag"
                            @change="changeCcValue"
                        >
                            <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>

                    <el-form-item v-if="cmEnable" style="width: 100%">
                        <span
                            class="select"
                            title="点击选择密送人"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            style="width: 60px; min-width: 60px; text-align: right; margin-right: 16px"
                            @click="selectPersonDialog('bcc')"
                            >密送</span
                        >
                        <el-select
                            v-model="emailValue.email.bccEmailAddressList"
                            multiple
                            filterable
                            remote
                            :reserve-keyword="false"
                            placeholder="请选择密送人"
                            :remote-method="searchMethod"
                            :loading="loading"
                            size="large"
                            class="box-input"
                            @remove-tag="removeBccEmailOrgUnitListTag"
                            @change="changeBccValue"
                        >
                            <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>

                    <div style="margin-bottom: 18px">
                        <el-upload
                            class="upload-demo"
                            action=""
                            :show-file-list="false"
                            multiple
                            :http-request="uploadAttachment"
                            content-type="multipart/form-data"
                            :limit="5"
                        >
                            <span
                                style="opacity: 0.5; cursor: pointer; font-size: 15px"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                ><i class="ri-attachment-2" style="font-size: 15px"></i>点击上传</span
                            >
                            <el-divider direction="vertical" />
                            <span
                                style="opacity: 0.5; cursor: pointer; font-size: 15px"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                >共计{{ emailValue.emailAttachmentDTOList?.length }}个附件</span
                            >
                        </el-upload>
                        <div v-for="attachment in emailValue.emailAttachmentDTOList" class="attachment-box">
                            <div :title="attachment.fileName" class="attachment-box">
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
                                        v-else=""
                                        src="@/assets/images/xiangqing/file-unknow-line.png"
                                        alt=""
                                        class="attachment-icon"
                                    />
                                    <div class="attachment-title-box">
                                        <i
                                            class="ri-close-circle-line"
                                            style="margin-left: 161px; cursor: pointer"
                                            @click="attachmentDel(attachment.fileName)"
                                        ></i>
                                        <div class="attachment-name">{{ attachment.fileName }}</div>
                                        <div class="attachment-usage">{{ attachment.displaySize }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <el-form-item style="width: 100%">
                        <!-- <myEditor ref="RefMyEditor" style="width: 100%" @valueHtml="emailValue.email.richText"></myEditor> -->
                        <div style="width: 100%">
                            <Editor
                                ref="editor"
                                v-model="emailValue.email.richText"
                                @getContent="getContent"
                                @getContentTxt="getContentTxt"
                            />
                        </div>
                    </el-form-item>
                </el-form>
            </div>
        </el-col>
    </el-row>

    <y9Dialog v-model:config="dialogConfig">
        <el-row :gutter="20">
            <el-col :span="15" class="left-tree">
                <div class="tree-div">
                    <div style="background-color: #fff">
                        <selectPerson :tree-api-obj="treeApiObj" @onTreeClick="onTreeTitleClick" />
                    </div>
                </div>
            </el-col>
            <el-col :span="9" class="right-list">
                <y9Table :config="y9TableConfig" @click="dele()"></y9Table>
            </el-col>
        </el-row>
    </y9Dialog>

    <div class="di-bottom">
        <el-button
            type="primary"
            class="global-btn-main"
            @click="saveOfSend('send')"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-send-plane-fill" style="vertical-align: middle; font-size: large; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('发送') }}</span>
        </el-button>
        <el-button
            class="global-btn-third"
            @click="saveOfSend('save')"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-save-line" style="vertical-align: middle; font-size: large; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('存草稿') }}</span>
        </el-button>
        <el-button
            type="primary"
            class="global-btn-third"
            @click="cancel()"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
        >
            <i class="ri-close-circle-line" style="vertical-align: middle; font-size: large; margin-right: 4px"></i>
            <span style="vertical-align: middle">{{ $t('关闭') }}</span>
        </el-button>
    </div>
</template>

<script lang="ts" setup>
    //固定模块样式
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    // 在 fiexHeader 变化时 监听滚动事件，改变className
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import { onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue';
    import Editor from '@/components/Editor/index.vue';
    import { forwardPageEmail, emailDetail, replyAllEmail, replyEmail, saveEmail, sendEmail } from '@/api/email/index';
    import router from '@/router';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { ElMessageBox, ElNotification } from 'element-plus';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import { addAttachment, deleteAttachment } from '@/api/email/attachment';

    const settingStore = useSettingStore();
    const layout = computed(() => settingStore.getLayout);
    const isFiexHeader = computed(() => settingStore.getFixedHeader);
    const { t } = useI18n();

    //数据
    const data = reactive({
        col: '',
        flagst: true,
        loading: false,
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_org_person', disabled: true }
            },
            search: {
                api: searchByName,
                params: {
                    key: '',
                    treeType: 'tree_type_org_person'
                }
            }
        },
        currTreeNodeInfo: {} //当前tree节点的信息
    });

    const { treeApiObj } = toRefs(data);

    // 封装邮件属性
    const emailValue = ref({
        attachmentList: [],
        email: {
            folder: '',
            messageId: '',
            replyMessageId: '',
            forwardMessageId: '',
            subject: '',
            richText: '',
            separate: false,
            text: '',
            toEmailAddressList: [],
            ccEmailAddressList: [],
            bccEmailAddressList: []
        },
        emailAttachmentDTOList: []
    });

    const editorRef = shallowRef();

    const getContent = (v: string) => {
        emailValue.value.email.richText = v;
    };

    const getContentTxt = (v: string) => {
        emailValue.value.email.text = v;
    };

    const mode = 'default';

    // 组件销毁时，也及时销毁编辑器
    onBeforeUnmount(() => {
        const editor = editorRef.value;
        if (editor == null) return;
        editor.destroy();
    });

    //删除附件
    async function attachmentDel(fileName) {
        let result = await deleteAttachment(emailValue.value.email.folder, emailValue.value.email.messageId, fileName);
        if (result.success) {
            for (var i = 0; i < emailValue.value.emailAttachmentDTOList.length; i++) {
                console.log(emailValue.value.emailAttachmentDTOList[i]);
                if (fileName == emailValue.value.emailAttachmentDTOList[i].fileName) {
                    emailValue.value.emailAttachmentDTOList.splice(i, 1);
                    return;
                }
            }
        }
    }

    async function uploadAttachment(params) {
        let result;
        if (emailValue.value.email.messageId) {
            result = await addAttachment(emailValue.value.email.folder, emailValue.value.email.messageId, params.file);
        } else {
            // 没保存过先保存 主要是为了能拿到 messageId
            if (await saveOfSend('save')) {
                result = await addAttachment(
                    emailValue.value.email.folder,
                    emailValue.value.email.messageId,
                    params.file
                );
            }
        }
        if (result.success) {
            emailValue.value.emailAttachmentDTOList.push(result.data);
            if (emailValue.value.email.subject == '') {
                emailValue.value.email.subject = result.data.fileName;
            }
        }
    }

    //下载
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

    const handleCreated = (editor) => {
        editorRef.value = editor; // 记录 editor 实例，重要！
    };

    const handleChange = (editor) => {
        emailValue.value.email.richText = editor.getHtml();
        emailValue.value.email.text = editor.getText();
        console.log('change:', editor.children);
    };

    const input = ref('');
    const toEnable = ref(true);
    const cmEnable = ref(false);
    const ccEnable = ref(false);
    const bccEnable = ref(false);
    const recentContactEnable = ref(false);

    //下拉框相关对象
    interface ListItem {
        value: string;
        label: string;
    }

    const list = ref<ListItem[]>([]);
    const options = ref<ListItem[]>([]);
    const loading = ref(false);

    const formInline = reactive({
        user: '',
        region: ''
    });

    onMounted(() => {
        const layout = computed(() => settingStore.getLayout);
        if (layout.value === 'Y9Horizontal') {
            document.getElementById('indexlayout').style.height = 'auto';
            document.getElementById('containerDiv').className = 'a';
            document.getElementsByClassName('item-form')[0].style.marginTop = '0px';
            document.getElementsByClassName('di-bottom')[0].style.marginBottom = '10px';
        } else {
            document.getElementById('indexlayout').style.height = '';
        }
        if (!isFiexHeader.value) {
            window.addEventListener('scroll', listener, false);
        }

        if (router.currentRoute.value.query.uid != null) {
            console.log(router.currentRoute.value.path + '地址');
            let path = router.currentRoute.value.path;
            let uid = router.currentRoute.value.query.uid;
            let folder = router.currentRoute.value.query.folder;
            console.log('path=' + path);
            if (path.indexOf('replyAll') >= 0) {
                console.log('进去了replyAll');
                replyAll(folder, uid);
            } else if (path.indexOf('reply') >= 0) {
                console.log('进去了reply');
                reply(folder, uid);
            } else if (path.indexOf('forward') >= 0) {
                console.log('进去了forward');
                forwardEmail(folder, uid);
            } else if (path.indexOf('writing') >= 0) {
                draftDetail(folder, uid);
            }
        }
        const paramsStr = window.location.search;
        if (paramsStr != null && paramsStr != '') {
            const params = new URLSearchParams(paramsStr);
            let email = params.get('email');
            emailValue.value.email.toEmailAddressList.push(email);
        }
    });

    //转发
    async function forwardEmail(folder, uid) {
        let forwardPageData = await forwardPageEmail(folder, uid);
        console.log(forwardPageData.data.messageId);

        for (const key in emailValue.value.email) {
            emailValue.value.email[key] = forwardPageData.data[key];
        }
        emailValue.value.emailAttachmentDTOList = forwardPageData.data.emailAttachmentDTOList;
    }

    //打开草稿
    async function draftDetail(folder, uid) {
        let result = await emailDetail(folder, uid);

        // emailValue.value.email = result.data;
        for (const key in emailValue.value.email) {
            emailValue.value.email[key] = result.data[key];
        }
        if (
            emailValue.value.email.ccEmailAddressList.length > 0 ||
            emailValue.value.email.bccEmailAddressList.length > 0
        ) {
            cmEnable.value = true;
        }
        emailValue.value.emailAttachmentDTOList = result.data.emailAttachmentDTOList;

        if (result.data.toEmailAddressList != null) {
            for (var i = 0; i < result.data.toEmailAddressList.length; i++) {
                toValue.value.push(result.data.toEmailAddressList[i]);
            }
        }
    }

    //回复
    async function reply(fodler, uid) {
        let replyData = await replyEmail(fodler, uid);
        for (const key in emailValue.value.email) {
            emailValue.value.email[key] = replyData.data[key];
        }
        if (replyData.data.toEmailAddressList != null) {
            for (var i = 0; i < replyData.data.toEmailAddressList.length; i++) {
                toValue.value.push(replyData.data.toEmailAddressList[i]);
            }
        }
    }

    //全部回复
    async function replyAll(folder, uid) {
        let replyAllData = await replyAllEmail(folder, uid);
        for (const key in emailValue.value.email) {
            emailValue.value.email[key] = replyAllData.data[key];
        }
    }

    function removeTag(params) {}

    function removeCcEmailOrgUnitListTag(params) {}

    function removeBccEmailOrgUnitListTag(params) {}

    const ChangeName = ref();

    function changeValue(val) {
        console.log(val);
    }

    function changeCcValue(val) {}

    function changeBccValue(val) {}

    // 收件人等下拉框内容改动事件监听，配合remote-method属性使用，query为输入框当前输入的内容，通过后台接口返回相关数据，并过滤
    async function searchMethod(params) {
        console.log('姓名为' + params);
        if (params != '') {
            if (params.indexOf('@') > 0) {
                //填充常用邮箱
                var arr = [{ value: '' + params, lable: '' + params }];
                list.value = arr.map((item) => {
                    return { value: item.value, lable: item.lable };
                });
                ChangeName.value = list.value;
                options.value = list.value;
            } else {
                // debugger;
                loading.value = true;
                let searchData = await searchByName({ key: params, treeType: 'tree_type_person' });
                ChangeName.value = searchData.data;
                if (searchData.success) {
                    for (var i = 0; i < searchData.data.length; i++) {
                        console.log('toValue.value' + toValue.value);
                        console.log('searchData.data[0]======' + searchData.data[i].name);
                        if (toValue.value != searchData.data[i].name && searchData.data[i].orgType == 'Person') {
                            list.value = searchData.data
                                .filter((item) => {
                                    return item.email != null;
                                })
                                .map((item) => {
                                    return { value: `${item.email}`, label: `${item.name}` };
                                });
                        } else {
                            loading.value = false;
                        }
                    }
                    loading.value = false;
                    options.value = list.value.filter((item) => {
                        return item.label.toLowerCase().includes(params?.toLowerCase());
                    });
                } else {
                    loading.value = false;
                    options.value = [];
                }
            }
        }
    }

    let y9TableConfig = ref({
        columns: [
            {
                title: '名称',
                key: 'name'
            },
            {
                title: '操作',
                render: (row) => {
                    return h(
                        'span',
                        {
                            onClick: () => {
                                del(row);
                            }
                        },
                        '删除'
                    );
                }
            }
        ],
        rowKey: 'id',
        tableData: [],
        pageConfig: false,
        border: false
    });

    //点击tree的回调
    async function onTreeTitleClick(node) {
        let list = y9TableConfig.value.tableData.filter((item) => item.id === node.email);
        if (list?.length) {
            //删除
            y9TableConfig.value.tableData.splice(
                y9TableConfig.value.tableData.findIndex((item) => item.id === node.id),
                1
            );
        } else if (node.orgType === 'Person') {
            if (node.email != null) {
                y9TableConfig.value.tableData.push({
                    name: node.name,
                    id: node.email
                });
            } else {
                ElNotification({
                    type: 'warning',
                    message: '选中的目标未注册邮箱'
                });
            }
        }
    }

    //删除选中的联系人
    function del(row) {
        console.log(row);
        y9TableConfig.value.tableData.splice(
            y9TableConfig.value.tableData.findIndex((item) => item.id === row.id),
            1
        );
    }

    let dialogConfig = ref({
        show: false,
        width: '50%',
        title: '添加收件人',
        okText: '确定',
        cancelText: '取消',
        resetText: false,
        onOk: (config) => {
            //可选形式一：返回promise的形式：
            return new Promise((resolve, reject) => {
                for (var i = 0; i < y9TableConfig.value.tableData.length; i++) {
                    let emailAddress = y9TableConfig.value.tableData[i].id;
                    if (toEnable.value && !emailValue.value.email.toEmailAddressList.includes(emailAddress)) {
                        emailValue.value.email.toEmailAddressList.push(emailAddress);
                    }
                    if (ccEnable.value && !emailValue.value.email.ccEmailAddressList.includes(emailAddress)) {
                        emailValue.value.email.ccEmailAddressList.push(emailAddress);
                    }
                    if (bccEnable.value && !emailValue.value.email.bccEmailAddressList.includes(emailAddress)) {
                        emailValue.value.email.bccEmailAddressList.push(emailAddress);
                    }
                }
                y9TableConfig.value.tableData = [];
                resolve();
            });
        },
        onReset: (config) => {
            console.log('重置按钮被点击了', config);
        },
        visibleChange: (visible) => {
            console.log('弹窗的显示关闭状态为', visible);
        }
    });

    function toggleCC() {
        cmEnable.value = !cmEnable.value;
    }

    function selectPersonDialog(type) {
        //清空上次选择
        y9TableConfig.value.tableData = [];

        console.info('选择人员类型：' + type);
        if (type == 'to') {
            toEnable.value = true;
            ccEnable.value = false;
            bccEnable.value = false;
            dialogConfig.value.title = '请选择收件人';
        } else if (type == 'cc') {
            toEnable.value = false;
            ccEnable.value = true;
            bccEnable.value = false;
            dialogConfig.value.title = '请选择抄送人';
        } else if (type == 'bcc') {
            toEnable.value = false;
            ccEnable.value = false;
            bccEnable.value = true;
            dialogConfig.value.title = '请选择密送人';
        }
        Object.assign(dialogConfig.value, {
            show: true
        });
    }

    function toLine() {
        recentContactEnable.value = !recentContactEnable.value;
    }

    //存草稿save()
    async function saveOfSend(type) {
        if (emailValue.value.email.subject == '') {
            ElMessageBox({ title: '提示', message: '请填写主题' });
            return;
        }
        if (emailValue.value.email.toEmailAddressList.length == 0) {
            ElMessageBox({ title: '提示', message: '请选择收件人' });
            return;
        }
        // debugger;
        let flag = false;
        let result = await saveEmail(emailValue.value.email);
        if (result.success) {
            emailValue.value.email.messageId = result.data;
            flag = true;
            if (type == 'send') {
                let sendResult = await sendEmail(emailValue.value.email.messageId);
                if (sendResult.success) {
                    ElNotification({
                        title: t('成功'), //result.success ? t('成功') : t('失败'),
                        message: sendResult.msg, //result.msg,
                        type: 'success', //result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    router.push({ path: '/sent' });
                }
            } else {
                ElNotification({
                    title: t('成功'), //result.success ? t('成功') : t('失败'),
                    message: result.msg, //result.msg,
                    type: 'success', //result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                console.log('结果id为' + emailValue.value.email.messageId);
            }

            //
        } else {
            ElNotification({
                title: t('失败'), //result.success ? t('成功') : t('失败'),
                message: result.msg, //result.msg,
                type: 'success', //result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        }
        return flag;
    }

    //取消cancel()
    function cancel() {
        ElMessageBox.confirm(`${t('是否保存该邮件')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                let result = await saveEmail({
                    richText: emailValue.value.email.richText,
                    text: emailValue.value.email.text,
                    task: true,
                    subject: emailValue.value.email.subject,
                    separate: false
                });

                if (result.success) {
                    ElNotification({
                        title: t('成功'), //result.success ? t('成功') : t('失败'),
                        message: result.msg, //result.msg,
                        type: 'success', //result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    router.push({ path: '/draft' });
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消'),
                    offset: 65
                });
                router.go(-1);
            });
    }
</script>

<style lang="scss" scoped>
    .a {
        margin-top: 18px;
    }

    .contain {
        margin-top: 60px;
    }

    .add-receiver {
        position: absolute;
        right: 5px;
        bottom: 5px;
        font-size: 28px;
        cursor: pointer;
    }

    .select {
        cursor: pointer;

        color: var(--el-color-primary);
    }

    .left-tree {
        border: 1px solid lightgrey;
        height: 400px;
    }

    .tree-toolbar {
        padding: 10px 0;
    }

    .tree-toolbar .el-input {
        width: 61.5%;
    }

    .tree-div {
        width: 100%;
        height: 380px;
        overflow-y: auto;
    }

    .mytree {
        overflow-x: hidden;
        height: 100%;
    }

    .mytree li {
        line-height: 30px;
    }

    .mytree a {
        width: 100%;
        border-bottom: 1px solid #ccc;
        line-height: 30px;
        height: 30px;
    }

    .mytree li .curSelectedNode {
        background-color: #e5e5e5;
    }

    .right-list {
        border: 1px solid lightgrey;
        height: 400px;
    }

    .personTree .el-menu--horizontal > .el-menu-item {
        height: 40px;
        line-height: 40px;
    }

    .lineBtn {
        float: right;
        //margin-left: calc(100% - 500px);
    }

    .text {
        font-size: 14px;
    }

    .item {
        padding: 18px 0;
    }

    .box-card {
        height: 100%;
        box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
    }

    .box-input {
        width: calc(100% - 60px - 16px) !important;
    }
    :deep(.el-input__wrapper) {
        font-size: v-bind('fontSizeObj.baseFontSize') !important;
    }
    .xiexin-contacts-head {
        height: 50px;
        line-height: 50px;
        text-align: center;
        border-bottom: 1px solid var(--el-color-primary);
        font-size: 16px;
        color: #697bb9;
    }

    .infinite-list {
        // height: calc(100% - 60px - 80px - 32px - 20px - 35px);
        height: 615px;
        padding: 0;
        margin: 0;
        list-style: none;
    }

    .infinite-list .infinite-list-item {
        display: flex;
        align-items: center;
        justify-content: left;
        height: 62px;
        color: var(--el-color-primary);
    }

    .infinite-list .infinite-list-item + .list-item {
        margin-top: 10px;
    }

    .el-row {
        margin-bottom: 20px;
    }

    el-row:last-child {
        margin-bottom: 0;
    }

    .el-col {
        border-radius: 4px;
    }

    .item-form {
        width: 100%;
        margin-top: 20px;
    }
    :deep(.el-form-item__content .select) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
    .di-bottom {
        margin-bottom: 0px;
    }

    :deep(.el-tag) {
        color: var(--el-color-primary);
    }

    :deep(.el-tag__close) {
        color: var(--el-color-primary);
    }

    :deep(.y9-dialog-footer) {
        margin-right: 15px;
    }

    :deep(.cell) {
        line-height: 36px;
    }

    :deep(.el-tag--info) {
        --el-tag-hover-color: var(--el-color-primary);
    }

    .w-e-bar-item {
        padding: 0px;
    }

    .upload-demo {
        margin-bottom: 8px;
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
        width: 248px;
        margin-right: 8px;
        display: inline-block;
        opacity: 0.8;
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
        height: 60px;
        margin-left: 10px;
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
        color: #030303;
        opacity: 0.9;
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .attachment-usage {
        font-size: v-bind('fontSizeObj.baseFontSize');
        color: #000;
        opacity: 0.6;
    }

    .attachment-del {
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        line-height: 20px;
        font-size: v-bind('fontSizeObj.baseFontSize');
        color: #030303;
        opacity: 0.9;
        cursor: pointer;
    }

    :deep(.tox-tinymce--toolbar-sticky-off) {
        height: 680px !important;
    }
    :deep(.tox .tox-mbtn) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
