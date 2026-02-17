import { useEffect, useState } from 'react';

export function useMediaQuery(query: string) {
  const [matches, setMatches] = useState<boolean>(
    window.matchMedia(query).matches
  );
  useEffect(() => {
    const media = window.matchMedia(query);

    const handleMatchMediaChange = () => {
      setMatches(media.matches);
    };

    media.addEventListener('change', handleMatchMediaChange);
    return () => media.removeEventListener('change', handleMatchMediaChange);
  }, [query]);
  return matches;
}
