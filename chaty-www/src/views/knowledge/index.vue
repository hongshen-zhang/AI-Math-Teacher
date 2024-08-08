<template>
    <div class="container">
        <el-form class="header-bar" :inline="true">
            <el-form-item>
                <el-input v-model="filter.keyword" placeholder="请输入关键字搜索" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="loadData">查询</el-button>
                <el-button type="primary" @click="handleAdd">新增</el-button>
            </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="tableData" style="width: 100%; user-select: text;" :border="true" size="large" class="main-table"
            empty-text="无数据">
            <template v-for="column in columns" :key="column.prop">
                <el-table-column v-bind="column">
                    <template v-if="column.prop === 'operations'" #default="scope">
                        <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
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
        <form-dialog ref="formDialog" @onClose="loadData"></form-dialog>
    </div>
</template>

<script>
import FormDialog from './form-dialog.vue'

export default {
    components: {
        FormDialog
    },
    data() {
        return {
            columns: [
                {
                    prop: "name",
                    label: "名称",
                },
                {
                    prop: "content",
                    label: "知识点",
                    type: 'textarea',
                },
                {
                    prop: 'operations',
                    label: '操作',
                }
            ],
            filter: {
                keyword: ''
            },
            tableData: [],
            loading: false,
        }
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            this.loading = true
            let params = {}
            if (this.filter.keyword && this.filter.keyword !== '') {
                params.keyword = this.filter.keyword
            }
            this.$axios.get("/api/knowledge/list", { params }).then(res => {
                this.tableData = res.data
            }).finally(() => this.loading = false)
        },
        handleAdd() {
            this.$refs.formDialog.show()
        },
        handleEdit(data) {
            this.$refs.formDialog.show(data)
        },
        handleDelete(id) {
        }
    }
}
</script>

<style lang="scss" scoped>
.container {
    display: flex;
    flex-direction: column;
    height: 100%;

    .header-bar {
        padding: 10px;
    }

    .main-table {
        flex: 1;
    }
}
</style>