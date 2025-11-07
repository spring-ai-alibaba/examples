import { atom } from 'jotai';

export type ThemeMode = 'light' | 'dark' | 'system';
export const themeModeAtom = atom<ThemeMode>('system');

// 创建实际主题状态（考虑系统主题）
export const actualThemeAtom = atom<'light' | 'dark'>(get => {
  const themeMode = get(themeModeAtom);
  
  if (themeMode === 'system') {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
  
  return themeMode;
});

// 监听系统主题变化的函数
export const setupThemeListener = (callback: () => void) => {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  
  // 添加变化监听器
  const listener = () => callback();
  mediaQuery.addEventListener('change', listener);

  return () => mediaQuery.removeEventListener('change', listener);
}; 
