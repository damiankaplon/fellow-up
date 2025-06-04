import {Location} from "./Location.ts";
import {Fellow} from "./Fellow.ts";

export type Proposal = {
  acceptedBy: Fellow[];
  location: Location;
}
