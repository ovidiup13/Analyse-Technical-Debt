export interface Repository {
  id: string;
  name: string;
  author: string;
  uri: string;
  issueTrackerURI?: string;
  buildCommand?: string;
  projectFolder?: string;
}
