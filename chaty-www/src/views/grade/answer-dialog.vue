<template>
    <el-dialog :model-value="isShow" title="添加答案" width="600" :before-close="() => $emit('onClose')">
        <el-form label-width="120px">
            <el-form-item label="答案: " prop="answer" :style="{ width }">
                <el-input type="textarea" rows="5" v-model="answer" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="$emit('onClose', answer)">确认</el-button>
                <el-button type="primary" @click="$emit('onClose')">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script>
export default {
    props: {
        isShow: {
            type: Boolean,
            default: false,
        },
    },
    data() {
        return {
            answer: '',
            width: '500px',
        }
    },
    watch: {
        isShow(val) {
            if (val) {
                this.answer = ''
            }
        }
    },
}
</script>

<style lang="scss" scoped></style>