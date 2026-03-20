export interface SettingsFormInput {
  name: string;
  description: string;
  vote_interval: {
    value: number;
    unit: 'minutes' | 'hours' | 'weeks' | 'days';
  };
  votes_for_interval: number;
}
