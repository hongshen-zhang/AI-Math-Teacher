import { useUserStore } from "../store";

export default {
    mounted(el, binding) {
        const userStore = useUserStore();

        // 检查绑定的权限值
        const permission = binding.value;
        const roleAuths = userStore.getRoleAuths;

        console.log("roleAuths", roleAuths);

        if (!permission || !roleAuths.has(permission)) {
            // 如果用户没有权限，则移除该元素
            el.parentNode && el.parentNode.removeChild(el);
        }
    }
};
