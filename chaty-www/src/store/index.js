import { defineStore } from 'pinia'
import { getAuthData } from '@/api/auth'

export const useUserStore = defineStore("user", {
    state: () => ({
        user: null,
        school: null,
        roles: null,
        isShowLogin: false,
        locale: null,
        guideStatus: {
            markPaper: true,
            essay: true,
            packageConfig: true
        },
        aimodelOptions: {
            "gpt-4o": {value: "gpt-4o", label: "模型A", vision: true},
            "gpt-4o-2024-08-06": {value: "gpt-4o-2024-08-06", label: "模型B", vision: true, responseFormat: true},
            "gpt-4o-2024-08-06_3": {value: "gpt-4o-2024-08-06_3", label: "模型C", vision: true, responseFormat: true},
            "gpt-4o-mini": {value: "gpt-4o-mini", label: "模型D", vision: true, responseFormat: true},
            "gpt-4-vision-preview": {value: "gpt-4-vision-preview", label: "模型E", vision: true},
            "Claude 3 Opus": {value: "Claude 3 Opus", label: "模型F", vision: true},
            "Claude 3 Sonnet": {value: "Claude 3 Sonnet", label: "模型G", vision: true},
            "gpt-4-turbo": {value: "gpt-4-turbo", label: "模型H"},
            "gpt-4": {value: "gpt-4", label: "模型I"},
            "gpt-3.5-turbo": {value: "gpt-3.5-turbo", label: "模型J"},
            "ERNIE-Bot": {value: "ERNIE-Bot", label: "模型K"},
            "qwen-turbo": {value: "qwen-turbo", label: "模型L"},
            "qwen-plus": {value: "qwen-plus", label: "模型M"},
            "qwen-max": {value: "qwen-max", label: "模型N"},
            "gemini-2.0-flash-exp": {value: "gemini-2.0-flash-exp", label: "模型T"},
        },
        allXYOffsetDataById:{}
    }),
    getters: {
        getUser: (state) => {
            return state.user || JSON.parse(localStorage.getItem('user'));
        },
        getRoles: (state) => {
            return state.roles || JSON.parse(localStorage.getItem('roles'));
        },
        getSchool: (state) => {
            return state.school || JSON.parse(localStorage.getItem('school'));
        },
        getLocale: (state) => {
            let locale = state.locale || localStorage.getItem('locale');
            if (!locale) {
                locale = "cn"
                state.locale = locale
            }
            return locale
        },
        needMarkPaperGuide: (state) => state.guideStatus.markPaper,
        needEssayGuide: (state) => state.guideStatus.essay,
        needPackageConfigGuide: (state) => state.guideStatus.packageConfig,
        getAimodelOptions: (state) => {
            return state.aimodelOptions
        },
        getRoleAuths: (state) => {
            const menus = new Set()
            state.getRoles.forEach(role => {
                const roleAuth = role.roleAuth && JSON.parse(role.roleAuth)
                if (roleAuth && roleAuth.menus) {
                    roleAuth.menus.forEach(menu => {
                        menus.add(menu)
                    })
                }
            })
            return menus
        },
        getXYOffsetData: (state) => {
            return (id) => {
                return state.allXYOffsetDataById[id] || JSON.parse(localStorage.getItem('allXYOffsetDataById'))?.[id] ||  { xOffset: 0, yOffset: 0 }
            }
        },
    },
    actions: {
        setUser(user) {
            this.user = user;
            if (user) {
                localStorage.setItem('user', JSON.stringify(user));
            } else {
                localStorage.removeItem('user');
            }
        },
        showLogin(isShow) {
            this.isShowLogin = isShow;
        },
        setLocale(locale) {
            this.locale = locale
            localStorage.setItem("locale", locale)
        },
        setGuideStatus(page, status) {
            // page: 'markPaper' | 'essay' | 'packageConfig'
            // status: boolean
            this.guideStatus[page] = status;
            this.saveGuideStatus();
        },
        initGuideStatus() {
            let storedGuide = localStorage.getItem('guideStatus');
            if (storedGuide) {
                this.guideStatus = JSON.parse(storedGuide);
            } else {
                // 默认都需要引导（示例）
                this.guideStatus = {
                    markPaper: true,
                    essay: true,
                    packageConfig: true
                }
            }
        },
        saveGuideStatus() {
            localStorage.setItem('guideStatus', JSON.stringify(this.guideStatus));
        },
        setRoles(roles) {
            this.roles = roles;
            localStorage.setItem('roles', JSON.stringify(roles));
        },
        setSchool(school) {
            this.school = school;
            localStorage.setItem('school', JSON.stringify(school));
        },
        async initAuthData() {
            const authData = await getAuthData()
            console.log(authData)
            this.setRoles(authData.data.roles)
            this.setSchool(authData.data.school)
        },
        setXYOffsetData(id, data) {
            if (!id) {
                return
            }
            if (typeof data !== 'object' || data === null) {
                return
            }
            const { xOffset, yOffset } = data
            if (typeof xOffset !== 'number' || typeof yOffset !== 'number') {
                return
            }
            this.allXYOffsetDataById = {
                ...this.allXYOffsetDataById,
                [id]: { xOffset, yOffset }
            }
            this.saveXYOffsetData();
        },
        saveXYOffsetData() {
            try {
                localStorage.setItem('allXYOffsetDataById', JSON.stringify(this.allXYOffsetDataById));
            } catch (e) {
                console.error("Failed to save allXYOffsetDataById to localStorage:", e);
            }
        },
        removeXYOffsetData(id) {
            if (this.allXYOffsetDataById[id]) {
                const { [id]: _, ...rest } = this.allXYOffsetDataById
                this.allXYOffsetDataById = rest
                this.saveXYOffsetData()
            }
        },
    }
})
