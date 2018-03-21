export interface Issue {
  issueId: string;
  issueKey: string;
  repositoryId: string;
  type?: string;
  summary: string;
  description: string;
  assignee: string;
  storyPoints?: number;
  priority?: string;
  status: string;
  created: Date;
  closed?: Date;
  due?: Date;
  labels: string[];
  timeTracker?: {
    estimate: number;
    remaining: number;
    logged: number;
  };
}
