<template>
    <el-dialog :model-value="isShow" title="知识库" width="1000" :before-close="() => $emit('onClose')">
        <el-row>
            <el-input v-model="filter.question" placeholder="请输入题目">
                <template #append>
                    <el-button type="primary" @click="loadQuestions">查询</el-button>
                </template>
            </el-input>
        </el-row>
        <el-table v-loading="loading" :data="questions" style="width: 100%" height="500" empty-text="无数据">
            <template v-for="column in columns" :key="column.prop">
                <el-table-column v-bind="column">
                    <template v-if="column.prop === 'operations'" #default="scope">
                        <el-button @click="onSelected(scope.row)">选择</el-button>
                    </template>
                    <template v-else-if="column.type === 'textarea'" #default="scope">
                        <el-scrollbar max-height="200px">
                            <el-text style="white-space: pre-wrap; word-break: break-all;">
                                {{ scope.row[column.prop] }}
                            </el-text>
                        </el-scrollbar>
                    </template>
                </el-table-column>
            </template>
        </el-table>
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
            columns: [
                {
                    prop: "question",
                    label: "题目",
                    type: 'textarea',
                },
                {
                    prop: "answer",
                    label: "答案",
                    type: 'textarea',
                },
                {
                    prop: 'knowledge',
                    label: '知识点',
                    type: 'textarea',
                },
                {
                    prop: 'operations',
                    label: '操作',
                    width: 100
                }
            ],
            filter: {
                question: '',
            },
            questions: [],
            loading: false,
        }
    },
    watch: {
        isShow(val) {
            if (val) {
                this.loadQuestions()
            }
        }
    },
    methods: {
        loadQuestions() {
            this.loading = true
            let params = {}
            if (this.filter.question && this.filter.question !== '') {
                params.question = this.filter.question
            }
            this.$axios.get("/api/questionLibrary/findAll", { params }).then(res => {
                this.questions = res.data
            }).finally(() => {
                this.loading = false
            })
        },
        onSelected(row) {
            this.$emit('onSelected', row)
        }
    }
}
</script>

<style lang="scss" scoped></style>