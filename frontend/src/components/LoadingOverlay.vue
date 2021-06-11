<template>
  <div class="loadingOverlay">
    <v-progress-circular indeterminate color="primary" :size="100" :width="10"></v-progress-circular>
  </div>
</template>

<script>
export default {};
</script>

<style>
.loadingOverlay {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
  background: rgba(255, 255, 255, 0.5);
}
</style>