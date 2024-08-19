<template>
    <el-dropdown v-if="positionList?.length" :hide-on-click="true" class="user-el-dropdown" @command="onMenuClick">
        <div class="name" @click="(e) => e.preventDefault()">
            <!-- show & if 的vue指令 仅用于适配移动端 -->
            <div>
                <i class="ri-route-line"></i>
                <span>{{ $t('选择岗位') }}</span>
                <el-badge :value="totalCount" class="badge"></el-badge>
            </div>
        </div>
        <template #dropdown>
            <el-dropdown-menu>
                <div v-for="(item, index) in positionList" :key="index">
                    <el-dropdown-item :command="item">
                        <div class="el-dropdown-item">
                            <div>
                                <i class="ri-shield-user-line"></i>{{ item.positionName }}
                                <el-badge :value="item.todoCount" class="badge"></el-badge>
                            </div>
                        </div>
                    </el-dropdown-item>
                    <el-divider
                        v-if="index !== positionList.length - 1"
                        style="padding-bottom: 12px; margin: 0px; margin-top: 6px"
                    ></el-divider>
                </div>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
    <div v-else class="user-el-dropdown">
        <!-- show & if 的vue指令 仅用于适配移动端 -->
        <div class="name" style="margin-top: 4px; cursor: not-allowed; color: #aaa">
            <i class="ri-route-line"></i>
            <span>{{ $t('选择岗位') }}</span>
        </div>
    </div>
    <!-- <PersonInfo ref="personInfo"/> -->
</template>
<script lang="ts">
    import { defineComponent } from 'vue';
    import { useRouter } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import y9_storage from '@/utils/storage';
    import IconSvg from './IconSvg';
    import { getLoginInfo } from '@/api/home';

    // import PersonInfo from '@/views/personal/personInfo.vue';
    interface RightTopUserSetupData {
        userInfo: Object;
        initInfo: Object;
        departmentMapList: Object;
        onMenuClick: (event: any) => Promise<void>;
    }

    export default defineComponent({
        name: 'RightTopUser',
        components: {
            IconSvg
            // PersonInfo
        },
        setup(): RightTopUserSetupData {
            const settingStore = useSettingStore();

            const router = useRouter();
            // const personInfo = ref();
            // 获取当前登录用户信息
            const userInfo = y9_storage.getObjectItem('ssoUserInfo');
            const initInfo = y9_storage.getObjectItem('cmsInitInfo');
            const departmentMapList = y9_storage.getObjectItem('departmentMapList');
            // 岗位列表
            const positionList: any = JSON.parse(sessionStorage.getItem('positionList'));
            // 所有岗位的待办消息
            let totalCount = 0;
            positionList?.map((item) => {
                totalCount += item.todoCount;
            });

            // 点击菜单
            const onMenuClick = async (command: string) => {
                // console.log(command, '999');
                // 设置positionId
                sessionStorage.setItem('positionId', command?.positionId);
                // 设置 positionName
                sessionStorage.setItem('positionName', command?.positionName);
                // 设置 deptName
                sessionStorage.setItem('deptName', command?.parentName);
                let res = await getLoginInfo();
                sessionStorage.setItem('positionList', JSON.stringify(res.data.positionList));
                positionList.value = res.data.positionList;
                // 所有岗位的待办消息
                positionList?.map((item) => {
                    totalCount += item.todoCount;
                });
                window.location = window.location.origin + window.location.pathname;
            };
            return {
                settingStore,
                userInfo,
                initInfo,
                departmentMapList,
                onMenuClick,
                positionList,
                totalCount
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
        font-size: var(--el-font-size-base);
        display: flex;
        margin-top: 8px;
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
            font-size: 20px;
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
