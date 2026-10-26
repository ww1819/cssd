<template>
  <div class="page">
    <el-empty :description="(title || '功能') + '（功能待开发）'" />
  </div>
</template>
<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const title = computed(() => route.meta?.title || '')
</script>
