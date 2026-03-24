import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function parseDateTime(
  isoString: string | null | undefined,
  options: {
    timeZone?: string;
    locale?: string;
  } = {}
) {
  if (!isoString) return null;

  const {
    timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone,
    locale = 'ru-RU',
  } = options;

  const date = new Date(isoString);

  if (isNaN(date.getTime())) {
    console.warn('Invalid date:', isoString);
    return null;
  }

  const formatterDate = new Intl.DateTimeFormat(locale, {
    timeZone,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });

  const formatterTime = new Intl.DateTimeFormat(locale, {
    timeZone,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  });

  const [year, month, day] = formatterDate.format(date).split('.');

  return {
    originalISO: isoString,
    date: {
      year,
      month,
      day,
    },
    time: formatterTime.format(date),
    timeShort: formatterTime.format(date).slice(0, 5),
    fullDate: formatterDate.format(date),
    fullDateTime: `${formatterDate.format(date)} ${formatterTime.format(date)}`,
    weekday: new Intl.DateTimeFormat(locale, {
      timeZone,
      weekday: 'long',
    }).format(date),
  };
}
