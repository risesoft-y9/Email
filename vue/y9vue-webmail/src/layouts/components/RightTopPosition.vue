<template>
    <el-dropdown :hide-on-click="true" class="user-el-dropdown" @command="onMenuClick">
        <div class="name" @click="(e) => e.preventDefault()">
            <!-- show & if 的vue指令 仅用于适配移动端 -->
            <div>
                <i class="ri-route-line"></i>
                <span>选择岗位</span>
                <el-badge :value="4" class="badge"></el-badge>
            </div>
        </div>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item command="position1">
                    <div class="el-dropdown-item">
                        <div>
                            <i class="ri-shield-user-line"></i>{{ $t('岗位1') }}
                            <el-badge :value="3" class="badge"></el-badge>
                        </div>
                    </div>
                </el-dropdown-item>
                <el-divider style="padding-bottom: 12px; margin: 0px; margin-top: 6px"></el-divider>
                <el-dropdown-item command="position2">
                    <div class="el-dropdown-item">
                        <div> <i class="ri-shield-user-line"></i>{{ $t('岗位2') }} </div>
                    </div>
                </el-dropdown-item>
                <el-divider style="padding-bottom: 12px; margin: 0px; margin-top: 6px"></el-divider>
                <el-dropdown-item command="position3">
                    <div class="el-dropdown-item">
                        <div>
                            <i class="ri-shield-user-line"></i>{{ $t('岗位3') }}
                            <el-badge :value="1" class="badge"></el-badge>
                        </div>
                    </div>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
    <!-- <PersonInfo ref="personInfo"/> -->
</template>
<script lang="ts">
    import { ref, computed, ComputedRef, defineComponent } from 'vue';
    import { useRouter } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import y9_storage from '@/utils/storage';
    import IconSvg from './IconSvg';
    import { $y9_SSO } from '@/main';

    // import PersonInfo from '@/views/personal/personInfo.vue';
    interface RightTopUserSetupData {
        userInfo: Object;
        initInfo: Object;
        departmentMapList: Object;
        onMenuClick: (event: any) => Promise<void>;
      fontSizeObj: Object;
    }

    export default defineComponent({
        name: 'RightTopUser',
        components: {
            IconSvg
            // PersonInfo
        },
        setup(): RightTopUserSetupData {
            const settingStore = useSettingStore();
          // 注入 字体变量
          const fontSizeObj: any = inject('sizeObjInfo');
            const router = useRouter();
            // const personInfo = ref();
            // 获取当前登录用户信息
            const userInfo = JSON.parse(sessionStorage.getItem('ssoUserInfo'));
            const initInfo = y9_storage.getObjectItem('initInfo');
            const departmentMapList = y9_storage.getObjectItem('departmentMapList');
            // 点击菜单
            const onMenuClick = async (command: string) => {
                switch (command) {
                    case 'position1':
                        break;
                    case 'position2':
                        break;
                    case 'position3':
                        break;
                    default:
                        break;
                }
            };
            return {
                settingStore,
                userInfo,
                initInfo,
                departmentMapList,
                onMenuClick,
              fontSizeObj
                // personInfo
            };
        }
    });
</script>
<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    .user-el-dropdown {
        z-index: 9999;
        height: $headerHeight;

        :deep(.el-dropdown--default) {
            display: flex;
            align-items: center;
        }
    }

    .name {
        color: var(--el-text-color-primary);
      font-size: v-bind('fontSizeObj.baseFontSize');
        display: flex;
        margin-top: 12px;

        & > div {
            display: flex;
            justify-content: center;
            align-items: center;

            span {
                line-height: 20px;
                text-align: end;
            }
        }

        i {
          font-size: v-bind('fontSizeObj.extraLargeFont');
            line-height: 20px;
        }

        .badge {
            margin-left: 5px;
        }
    }

    .el-dropdown-item {
        div {
            width: 100%;
            display: flex;
            padding: 0 5px;
        }
    }

    :deep(.el-badge) {
        .el-badge__content {
            border: none;
        }

        sup {
            top: 0;
        }
    }
</style>
