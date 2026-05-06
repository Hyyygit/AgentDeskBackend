<template>
  <div class="page-container">
    <h2 style="margin-bottom: 16px">个人中心</h2>
    <el-card style="max-width: 600px">
      <el-form :model="form" label-width="100px" v-loading="loading">
        <el-form-item label="用户名"><el-input v-model="form.userName" disabled /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="部门"><el-input v-model="form.department" /></el-form-item>
        <el-form-item label="角色"><el-tag>{{ form.roleCode }}</el-tag></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSave">保存</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card style="max-width: 600px; margin-top: 16px">
      <template #header><h3>修改密码</h3></template>
      <el-form :model="pwdForm" label-width="100px">
        <el-form-item label="旧密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleChangePwd">修改密码</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, changePassword } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ userName: '', realName: '', email: '', department: '', roleCode: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })

onMounted(async () => { loading.value = true; try { if (!userStore.user) await userStore.fetchProfile(); if (userStore.user) Object.assign(form, userStore.user) } finally { loading.value = false } })

const handleSave = async () => { await updateProfile(form); ElMessage.success('保存成功'); userStore.fetchProfile() }
const handleChangePwd = async () => { if (!pwdForm.oldPassword || !pwdForm.newPassword) { ElMessage.warning('请输入密码'); return }; await changePassword(pwdForm); ElMessage.success('密码修改成功'); pwdForm.oldPassword = ''; pwdForm.newPassword = '' }
</script>
