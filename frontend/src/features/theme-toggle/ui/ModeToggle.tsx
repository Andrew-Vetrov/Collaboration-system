import { Button } from '@/shared/ui';
import { Moon, Sun } from 'lucide-react';
import { useLayoutEffect, useState } from 'react';

export function ModeToggle() {
  const [theme, setTheme] = useState<string>(
    () => localStorage.getItem('theme') || 'light'
  );

  useLayoutEffect(() => {
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    localStorage.setItem('theme', theme);
  }, [theme]);

  return (
    <Button onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
      {theme === 'light' ? <Moon /> : <Sun />}
    </Button>
  );
}
