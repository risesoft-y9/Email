import { defineStore } from 'pinia';
import { getAllFolderList } from '@/api/folder';
import { Folder } from '@/types/global';

export const useFolderStore = defineStore('folderStore', {
    state: () => {
        const customFolders: Folder[] = [];
        const defaultFolders: Folder[] = [];
        return {
            customFolders,
            defaultFolders
        };
    },
    getters: {
        // 获取所有自定义文件夹
        getCustomFolders: (state) => {
            return state.customFolders;
        },
        // 获取所有默认文件夹
        getDefaultFolders: (state) => {
            return state.defaultFolders;
        },
        getAllFolders: (state) => {
            return state.defaultFolders.concat(state.customFolders);
        }
    },
    actions: {
        async initAllFolders() {
            const result = await getAllFolderList();
            if (result.success) {
                this.customFolders = [];
                result.data.customFolderList.map((item) => {
                    this.customFolders.push({ name: item.name, fullName: item.fullName, title: item.title });
                });
                this.defaultFolders = [];
                result.data.defaultFolderList.map((item) => {
                    this.defaultFolders.push({ name: item.name, fullName: item.fullName, title: item.title });
                });
            }
        }
    }
});
