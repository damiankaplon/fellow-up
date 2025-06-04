import {Fellow} from "./Fellow.ts";
import {Proposal} from "./Proposal.ts";

export type Mediation = {
  category: string;
  fellows: Fellow[];
  proposals: Proposal[];
}
