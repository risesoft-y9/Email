<!--
 * @Author: 
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-08-19 12:20:08
 * @Description: 上传历史邮件信息
-->
<template>
    <!-- 上传历史邮件信息application/vnd.ms-excel -->
    <div style="min-height: 200px">
        <div style="margin-bottom: 10px">
            <el-upload
                ref="uploadRef"
                v-model:file-list="fileList"
                :auto-upload="false"
                :headers="headers"
                :http-request="httpRequest"
                accept=".eml"
                action=""
                multiple
            >
                <template #trigger>
                    <el-input :placeholder="$t('点击可选择文件')" style="margin-right: 10px"></el-input>
                </template>

                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    style="margin-left: 10px"
                    type="primary"
                    @click="submitUpload"
                >
                    <i :style="{ 'font-size': fontSizeObj.baseFontSize }" class="ri-file-upload-line"></i>
                    <span>{{ $t('上传') }}</span>
                </el-button>
            </el-upload>
        </div>
        <div :style="{ color: '#606266', 'font-size': fontSizeObj.baseFontSize }">
            {{ $t('(仅支持相应的EML文件)，最多选择20个文件') }}
        </div>
        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { inject, onMounted, reactive, ref, toRefs } from 'vue';
    import type { UploadInstance } from 'element-plus';
    import { ElNotification } from 'element-plus';
    import { importEml } from '@/api/importEml/index';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const uploadRef = ref<UploadInstance>();
    const data = reactive({
        fileList: [],
        headers: {},
        actions: import.meta.env.VUE_APP_CONTEXT + 'api/rest/impExp/importOrgXml'
    });

    const props = defineProps({
        refresh: Function,
        type: {
            type: String,
            default: 'xml' //类型有xml和xls
        },
        id: {
            //type=xls时，必传
            type: [String, Number]
        }
    });

    const emits = defineEmits(['update']);

    let loading = ref(false);

    let { fileList, headers, actions } = toRefs(data);

    const submitUpload = () => {
        if (fileList.value.length > 0) {
            uploadRef.value!.submit();
        } else {
            ElNotification({ title: t('失败'), message: t('请选择文件'), type: 'error', duration: 2000, offset: 80 });
        }
    };

    async function httpRequest(params) {
        loading.value = true;

        let result = { success: false, msg: '' };

        result = await importEml(params.file);

        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if (result.success) {
            // 重新刷新树 数据
            props.refresh && props.refresh();
            loading.value = false;
            emits('update');
        }
    }

    onMounted(() => {
        const access_token = y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        if (access_token) {
            headers.value['Authorization'] = 'Bearer ' + access_token;
        }
    });
</script>

<style lang="scss" scoped>
    .upload-demo {
        // width: 100%;
        display: flex;
        flex-direction: initial;
        float: left;
        flex-wrap: wrap;
        align-items: center;

        :deep(.el-upload-list__item-name) {
            text-align: center;
        }
    }
</style>
