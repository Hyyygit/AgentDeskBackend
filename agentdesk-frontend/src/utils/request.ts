import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/api'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

service.interceptors.response.use((response: AxiosResponse<ApiResponse>) => {
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.msg || 'Request failed')
    if (res.code === 401) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      router.push('/login')
    }
    return Promise.reject(new Error(res.msg || 'Error'))
  }
  return response
}, (error) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    router.push('/login')
  }
  ElMessage.error(error.message || 'Network error')
  return Promise.reject(error)
})

export default service
