import { throttle } from "../utils";

export const useThrottle = (func: (...args: any[]) => void, time: number) => {
  const throttledFunc = throttle(func, time);
  return throttledFunc;
};
