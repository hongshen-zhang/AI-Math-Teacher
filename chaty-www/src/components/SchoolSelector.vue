<template>
    <el-select ref="selectRef" class="selector" v-model="selected" placeholder="请选择学校" @change="onChange"
        suffix-icon="Switch" remote :remote-method="loadSchools" :loading="loading" :show-arrow="false" :offset="6"
        @focus="loadSchools">
        <template #prefix>
            <span>学校：</span>
        </template>
        <el-option v-for="item in schoolOptions" :key="item.schoolId" :label="item.schoolName" :value="item.schoolId">
            <div class="option-item">
                <span>{{ item.schoolName }}</span>
                <el-tag class="option-item-right" v-if="getUser.defaultSchool === item.schoolId">默认</el-tag>
                <el-button class="option-item-right" v-else type="text" size="small"
                    @click.stop="setDefault(item.schoolId)">设为默认</el-button>
            </div>
        </el-option>
    </el-select>
</template>

<script>
import { mapActions, mapState } from 'pinia';
import { useUserStore } from '../store';
import { getSchools, loginWithSchool } from '../api/auth';

export default {
    data() {
        return {
            selected: null,
            schoolOptions: [],
            loading: false
        }
    },
    created() {
        this.schoolOptions = [this.getSchool]
        this.selected = this.getSchool.schoolId
    },
    computed: {
        ...mapState(useUserStore, ['getSchool', 'getUser']),
    },
    methods: {
        ...mapActions(useUserStore, ['setSchool', 'setUser']),
        loadSchools() {
            this.loading = true
            getSchools().then(res => {
                const schools = res.data
                const selected = schools.find(item => item.schoolId === this.getSchool.schoolId)
                if (!selected) {
                    schools.push(this.getSchool)
                }
                this.schoolOptions = schools
            }).finally(() => {
                this.loading = false
            })
        },
        setDefault(schoolId) {
            this.$axios.post('/api/user/update', {
                id: this.getUser.id,
                defaultSchool: schoolId
            }).then(() => {
                this.$message.success('设置默认学校成功')
                this.getUser.defaultSchool = schoolId
                this.setUser(this.getUser)
            })
        },
        onChange(schoolId) {
            loginWithSchool(schoolId).then(() => {
                const school = this.schoolOptions.find(item => item.schoolId === schoolId)
                this.setSchool(school)
                this.$message.success('切换学校成功')
                // 刷新当前页面
                this.$router.go(0)
            })
        }
    },
}
</script>

<style lang="scss" scoped>
.selector {
    width: 240px;
}

.option-item {
    display: flex;
    align-items: center;
    margin-right: -12px;

    &-right {
        margin-left: auto;
    }
}
</style>