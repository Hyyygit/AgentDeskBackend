<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo" @click="router.push('/chat')">
        <img src="@/assets/logo.svg" v-if="!isCollapsed" />
        <span v-if="!isCollapsed">AgentDesk</span>
        <span v-else>AD</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        @select="handleMenuSelect"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <template v-for="item in menuItems" :key="item.path">
          <el-menu-item v-if="!item.hidden" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <Fold v-if="!isCollapsed" /><Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="!unreadCount" class="header-item">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleUserCommand" class="header-item">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.user?.realName || userStore.user?.userName || 'User' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Bell, Fold, Expand } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapsed = ref(false)
const unreadCount = ref(0)

const menuItems = computed(() => {
  const items = [
    { path: '/chat', title: '智能对话', icon: 'ChatDotRound', hidden: false },
    { path: '/tickets', title: '工单管理', icon: 'Document', hidden: false },
    { path: '/knowledge', title: '知识库', icon: 'Collection', hidden: false },
    { path: '/admin/dashboard', title: '管理后台', icon: 'Odometer', hidden: true, roles: ['ADMIN'] },
    { path: '/admin/audit', title: '审计日志', icon: 'Warning', hidden: true, roles: ['ADMIN'] }
  ]
  const role = userStore.user?.roleCode
  return items.filter(i => !i.roles || (role && i.roles.includes(role)))
})

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/tickets')) return '/tickets'
  if (path.startsWith('/knowledge')) return '/knowledge'
  if (path.startsWith('/admin')) return path
  return '/chat'
})

const handleMenuSelect = (path: string) => {
  router.push(path)
}

const handleUserCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

onMounted(async () => {
  if (userStore.token) {
    await userStore.fetchProfile()
  }
})
</script>

<style scoped>
.main-layout { height: 100vh; }
.sidebar { background: #304156; overflow: hidden; transition: width 0.3s; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; cursor: pointer; }
.logo img { width: 32px; height: 32px; margin-right: 8px; }
.header { background: #fff; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e4e7ed; padding: 0 20px; }
.header-left { display: flex; align-items: center; }
.collapse-btn { font-size: 20px; cursor: pointer; }
.header-right { display: flex; align-items: center; gap: 16px; }
.header-item { cursor: pointer; }
.user-info { display: flex; align-items: center; gap: 8px; }
.el-main { background: #f0f2f5; padding: 0; }
</style>
