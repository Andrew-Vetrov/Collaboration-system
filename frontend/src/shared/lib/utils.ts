import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function parseDateTime(time: string) {
  if (time == '') {
    return;
  }
  const splitted = time.split(new RegExp('T|Z'));
  if (splitted.length !== 3) {
    return;
  }
  const splittedDate = splitted[0].split('-');
  const splittedTime = splitted[1].split('.');
  return {
    time: splittedTime[0],
    date: {
      year: splittedDate[0],
      month: splittedDate[1],
      day: splittedDate[2],
    },
  };
}
