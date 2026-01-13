<template>
    <editor :id="tinymceId" v-model="myValue" :disabled="disabled" :init="init"></editor>
</template>

<script lang="ts" setup>
    //JS部分
    //在js中引入所需的主题和组件
    import tinymce from 'tinymce/tinymce';
    import Editor from '@tinymce/tinymce-vue';

    // 我注释这些代码了，我觉得是没有必要这里再次导入的，可以解决打包后的那个警告问题
    // 如果一定要这样导入，那个警告不是错误，第一次加载会慢点，性能问题，先不管它
    import 'tinymce/themes/silver';
    import 'tinymce/themes/silver/theme';
    import 'tinymce/icons/default'; //引入编辑器图标icon，不引入则不显示对应图标
    import 'tinymce/models/dom'; // 这里是个坑 一定要引入
    //在TinyMce.vue中接着引入相关插件
    import 'tinymce/icons/default/icons';

    import 'tinymce/plugins/advlist';
    //import 'tinymce/plugins/anchor';
    import 'tinymce/plugins/autolink';
    import 'tinymce/plugins/autoresize';
    import 'tinymce/plugins/autosave';
    import 'tinymce/plugins/code'; // 源码
    import 'tinymce/plugins/codesample';
    import 'tinymce/plugins/charmap';
    import 'tinymce/plugins/fullscreen'; //全屏
    import 'tinymce/plugins/help';
    import 'tinymce/plugins/image'; // 插入上传图片插件
    //import 'tinymce/plugins/insertdatetime';
    import 'tinymce/plugins/link';
    import 'tinymce/plugins/lists'; // 列表插件
    // import 'tinymce/plugins/media'; // 插入视频插件
    import 'tinymce/plugins/nonbreaking';
    import 'tinymce/plugins/pagebreak';
    import 'tinymce/plugins/preview';
    // import 'tinymce/plugins/quickbars';
    //import 'tinymce/plugins/save';
    import 'tinymce/plugins/searchreplace';
    import 'tinymce/plugins/table'; // 插入表格插件
    import 'tinymce/plugins/wordcount'; // 字数统计插件
    import './plugins/powerpaste/plugin.min';
    import './plugins/powerpaste/js/wordimport.js';

    //接下来定义编辑器所需要的插件数据
    import { reactive, ref } from 'vue';
    import { onMounted, watch } from '@vue/runtime-core';
    import { uploadFile } from '@/api/email/attachment';
    // import { updateImg } from '@/api/order/order'
    const emits = defineEmits(['getContent', 'getContentTxt']);

    //这里我选择将数据定义在props里面，方便在不同的页面也可以配置出不同的编辑器，当然也可以直接在组件中直接定义
    const props = defineProps({
        value: {
            type: String,
            default: () => {
                return '';
            }
        },
        baseUrl: {
            type: String,
            default: ''
        },
        disabled: {
            type: Boolean,
            default: false
        }
    });
    //用于接收外部传递进来的富文本
    const myValue = ref(props.value);
    const tinymceId = ref('vue-tinymce-' + +new Date() + ((Math.random() * 1000).toFixed(0) + ''));
    //定义一个对象 init初始化
    const init = reactive({
        selector: '#' + tinymceId.value, //富文本编辑器的id,
        language_url: import.meta.env.VUE_APP_HOST_INDEX + 'tinymce/langs/zh-Hans.js', // 语言包的路径，具体路径看自己的项目，文档后面附上中文js文件
        language: 'zh-Hans', //语言
        skin_url: import.meta.env.VUE_APP_HOST_INDEX + 'tinymce/skins/ui/oxide', // skin路径，具体路径看自己的项目
        content_css: import.meta.env.VUE_APP_HOST_INDEX + 'tinymce/skins/content/default/content.css', //以css文件方式自定义可编辑区域的css样式，css文件需自己创建并引入
        height: 800, //编辑器高度
        branding: false, //是否禁用“Powered by TinyMCE”
        promotion: false,
        //menubar: false,
        image_dimensions: false, //去除宽高属性
        plugins:
            'preview searchreplace autolink autosave code fullscreen image link codesample table pagebreak advlist lists wordcount powerpaste',
        menubar: 'file edit view insert format tools table',
        toolbar:
            'undo redo | bold italic underline strikethrough | fontselect fontsizeselect formatselect | alignleft aligncenter alignright alignjustify | outdent indent | numlist bullist | forecolor backcolor removeformat | pagebreak | charmap | preview | insertfile image link codesample | ltr rtl',
        toolbar_mode: 'wrap',
        font_formats: 'Arial=arial,helvetica,sans-serif; 宋体=SimSun; 微软雅黑=Microsoft Yahei; Impact=impact,chicago;', //字体
        fontsize_formats: '11px 12px 14px 16px 18px 24px 36px 48px 64px 72px', //文字大小
        paste_webkit_styles: 'all',
        paste_merge_formats: true,
        powerpaste_word_import: 'merge', // 参数可以是propmt, merge, clear，效果自行切换对比
        powerpaste_html_import: 'merge', // propmt, merge, clear
        powerpaste_allow_local_images: false,
        nonbreaking_force_tab: false,
        paste_auto_cleanup_on_paste: false,
        file_picker_types: 'file',

        toolbar_sticky: true,
        autosave_ask_before_unload: true,
        autosave_interval: '30s',
        autosave_retention: '2m',

        convert_urls: false,

        //图片上传
        images_upload_handler: (blobInfo, success, failure) =>
            new Promise((resolve, reject) => {
                if (blobInfo.blob().size / 1024 / 1024 > 5) {
                    failure({ message: '上传失败，图片大小请控制在 5M 以内', remove: true });
                    return;
                } else {
                    console.log(blobInfo);
                    var image = blobInfo.blob();
                    console.log(image);
                    uploadFile(image)
                        .then((res) => {
                            var url = res.data;
                            if (res.success) {
                                success(url); //上传成功，在成功函数里填入图片路径
                                return;
                            } else {
                                failure('HTTP Error: 上传失败' + res.msg);
                                return;
                            }
                        })
                        .catch((error) => {
                            reject('上传出错 ');
                            return;
                        });
                }
            }),

        // 文件上传
        file_picker_callback: (callback, value, meta) => {
            // Provide file and text for the link dialog
            //文件分类
            var filetype = '.pdf, .txt, .zip, .rar, .7z, .doc, .docx, .xls, .xlsx, .ppt, .pptx, .mp3, .mp4';
            //为不同插件指定文件类型
            switch (meta.filetype) {
                case 'image':
                    filetype = '.jpg, .jpeg, .png, .gif';
                    break;
                case 'media':
                    filetype = '.mp3, .mp4';
                    break;
                case 'file':
                default:
            }
            //模拟出一个input用于添加本地文件
            var input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', filetype);
            input.click();
            input.onchange = function () {
                var file = input.files[0];

                switch (meta.filetype) {
                    case 'image':
                        uploadFile(file)
                            .then((res) => {
                                callback(res.data.url, { alt: file.name });
                            })
                            .catch(() => {
                                alert('上传出错，服务器开小差了呢');
                            });

                        break;
                    case 'media':
                        uploadFile(file)
                            .then((res) => {
                                callback(res.data.url, { title: file.name });
                            })
                            .catch(() => {
                                alert('上传出错，服务器开小差了呢');
                            });

                        break;
                    case 'file':
                        uploadFile(file)
                            .then((res) => {
                                callback(res.data.url, { title: file.name, text: file.name });
                            })
                            .catch(() => {
                                alert('上传出错，服务器开小差了呢');
                            });

                        break;
                    default:
                }
            };
        }
    });

    //监听外部传递进来的的数据变化
    watch(
        () => props.value,
        () => {
            myValue.value = props.value;
            emits('getContent', myValue.value);
            var activeEditor = tinymce.activeEditor;
            var text = activeEditor.selection.getContent({ format: 'text' });
            emits('getContentTxt', text);
        }
    );
    //监听富文本中的数据变化
    watch(
        () => myValue.value,
        () => {
            emits('getContent', myValue.value);
            var activeEditor = tinymce.activeEditor;
            var text = activeEditor.getContent({ format: 'text' });
            emits('getContentTxt', text);
        }
    );
    //在onMounted中初始化编辑器
    onMounted(() => {
        tinymce.init({
            plugins: 'autoresize'
        });
    });
</script>
<style lang="scss">
    .tox .tox-dialog .tox-dialog__header {
        flex-shrink: 0;
        display: flex;
        justify-content: space-between;
        border-bottom: solid 1px #eee;
        padding: 16px;
    }

    .tox .tox-dialog__body-content {
        box-shadow: 2px 2px 2px 1px #0000000f;
    }

    .tox .tox-dialog__body-nav {
        padding: 16px 0 16px 16px !important;
        width: 80px;
    }

    .tox .tox-dialog__body-nav-item {
        width: 100%;
        text-align: center;
        line-height: 2 !important;
    }

    .tox .tox-dialog__body-nav-item--active {
        border-bottom: 2px solid #006ce7;
        color: #006ce7;
        background-color: var(--el-color-primary-light-9);
    }

    .tox .tox-label,
    .tox .tox-toolbar-label {
        line-height: 2 !important;
    }
</style>
