import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, getUserProfile } from '@/api'
import type { UserInfo } from '@/types/api'

export const useUserStore = defineStore('user', () => {
  const user = ref<UserInfo | null>(null)
  const token = ref<string>(localStorage.getItem('accessToken') || '')

  const login = async (username: string, password: string) => {
    const res = await loginApi({ username, password })
    const { accessToken, refreshToken, user: userInfo } = res.data.data
    token.value = accessToken
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)
    user.value = userInfo
  }

  const fetchProfile = async () => {
    const res = await getUserProfile()
    user.value = res.data.data
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  return { user, token, login, fetchProfile, logout }
})
