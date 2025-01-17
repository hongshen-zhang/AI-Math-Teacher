<template>
  <div class="menu-bar">
    <div class="menu-left" @click="toHome">
      <el-image class="logo" src="/logo.png" />
      <div class="title">纸质作业留痕批改机器</div>
    </div>
    <div class="flex-grow"></div>

    <div class="menu-right">
      <el-image src="/icon/miaomiaoCaoZuo.png" class="left-image" @click="redirectToExternalLink"></el-image>
      <SchoolSelector style="margin-left: 20px" />
      <el-image src="/icon/avatar.png" class="i18n-icon"></el-image>
      <template v-if="getUser">
        <el-dropdown @command="onUserActions">
          <el-text type="primary" class="text">{{
  getUser.username
}}
          </el-text>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">{{ $t("common.logout") }}</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-button type="primary" text="primary" class="text" link @click="doLogin">{{
          $t('login.login')
          }}
        </el-button>
      </template>
    </div>
  </div>
</template>

<script>
import {useUserStore} from "@/store/index";
import { mapState, mapActions } from "pinia";
import SchoolSelector from "../../components/SchoolSelector.vue";
import { getSchools, loginWithSchool } from '@/api/auth';
export default {
  components: {
    SchoolSelector
  },
  data() {
    return {
      activeIndex: '/review',
      locales: [
        {prop: "cn", name: "中文"},
        {prop: "en", name: "English"},
      ]
    }
  },
  computed: {
    ...mapState(useUserStore, ['getUser', 'isShowLogin', 'getLocale']),
  },
  created() {
    this.activeIndex = this.$route.path
    this.$i18n.locale = this.getLocale
  },
  methods: {
    redirectToExternalLink() {
      window.location.href = 'https://scnduqrj7624.feishu.cn/wiki/LTCmwHSp1ieUNAk600icuuJFnch';
    },
    ...mapActions(useUserStore, ['showLogin', 'setUser', 'setLocale']),
    doLogin() {
      // this.showLogin(true)
      this.$router.push({path: '/login'})
    },
    onUserActions(command) {
      if (command === 'logout') {
        this.doLogout()
      }
    },
    doLogout() {
      this.$axios.post("/api/logout").then(res => {
        this.$message.success(this.$t('login.logoutSuccess'))
        this.setUser(null)
        this.$router.push("/login")
      })
    },
    toHome() {
      this.$router.push("/")
    },
    onLocaleChange(locale) {
      this.$i18n.locale = locale
      this.setLocale(locale)
    }
  }
}
</script>

<style lang="scss" scoped>
.menu-bar {
  height: 62px;
  background-image: linear-gradient(270deg, #2D7CF7 0%, #2EB5FF 98%);
  display: flex;
  width: 100%;
  padding: 0px 26px;
  z-index: 10;
  .menu-left {
    display: flex;
    align-items: center;
    column-gap: 5px;
    cursor: pointer;

    .logo {
      width: 40px;
    }

    .title {
      font-weight: 700;
      font-size: 18px;
      color: #FFFFFF;
      letter-spacing: 1.5px;
    }
  }

  .flex-grow {
    flex-grow: 1;
  }

  .menu-right {
    display: flex;
    align-items: center;
    margin-left: 20px;
    .left-image {
      cursor: pointer;
      background-image: linear-gradient(270deg, #ABF478 0%, #F2FED7 98%);
      box-shadow: 0 2px 4px 0 #c5c5c580;
      border-radius: 14px;
      width: 185.89px;
      height: 30px;
    }
    .i18n-icon {
      width: 26px;
      height: 26px;
      margin-right: 4.5px;
      margin-left: 42px;
    }
    .text {
      font-weight: 400;
      font-size: 14px;
      color: #FFFFFF;
      letter-spacing: 0;
    }
  }
}

</style>