<template>
    <div class="item">
        <i class="ri-map-pin-line"></i>
        <span>{{ deptName }}</span>
    </div>
    <div class="item">
        <!-- <RightTopUser /> -->
        <i class="ri-user-line"></i>
        <!-- show & if 的vue指令 仅用于适配移动端 -->
        <div v-show="settingStore.getWindowWidth > 425">
            <span>{{ $t(`${userInfo.name}`) }}</span>
        </div>
        <el-avatar
            v-if="settingStore.device === 'mobile'"
            :src="userInfo.avator ? userInfo.avator : ''"
            :style="{
                'font-size': fontSizeObj.baseFontSize,
                'background-color': 'var(--el-color-primary)',
                'margin-top': '8px'
            }"
        >
            {{ userInfo.loginName }}
        </el-avatar>
    </div>
</template>
<script lang="ts" setup>
    import { inject, onMounted, ref } from 'vue';
    import { useRouter } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import y9_storage from '@/utils/storage';

    const settingStore = useSettingStore();
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');
    const router = useRouter();
    // const personInfo = ref();
    // 获取当前登录用户信息
    const userInfo = y9_storage.getObjectItem('ssoUserInfo');
    const dn = userInfo?.dn;
    let deptName = ref('');

    onMounted(() => {
        if (dn.indexOf(',ou=') != -1) {
            deptName.value = dn.substring(dn.indexOf(',ou=') + 4);
            deptName.value = deptName.value.substring(0, deptName.value.indexOf(','));
        } else {
            deptName.value = dn.substring(dn.indexOf(',o=') + 3, dn.length);
        }
    });
</script>
<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    .item {
        overflow: hidden;
        padding: 0 11px;
        min-width: 5px;
        color: var(--el-menu-text-color);
        cursor: pointer;
        display: flex;
        align-content: center;

        i {
            position: relative;
            font-size: v-bind('fontSizeObj.extraLargeFont');
            // top: 4px;
        }

        span {
            font-size: v-bind('fontSizeObj.baseFontSize');
            margin-left: 5px;
        }

        &:hover {
            border-bottom: 2px solid var(--el-border-color-light);
            color: var(--el-color-primary);
        }

        &:hover {
            cursor: pointer;
            border-bottom: none; // 鼠标划过或点击时不显示下划线
        }

        .name {
            color: var(--el-text-color-primary);
            display: flex;
            justify-content: center;
            align-items: center;

            & > div {
                display: flex;
                justify-content: end;

                span {
                    line-height: 20px;
                    text-align: end;
                }
            }

            i {
                line-height: 20px;
                top: 0px;
            }

            .badge {
                margin-left: 5px;
            }
        }

        /**当前岗位 */
        &.notify {
            .badge {
                z-index: 1;

                & > .el-badge__content--danger {
                    background-color: var(--el-color-danger);
                }
            }
        }
    }

    .user-el-dropdown {
        z-index: 9999;
        height: $headerHeight;

        :deep(.el-dropdown--default) {
            display: flex;
            align-items: center;
        }
    }
</style>
