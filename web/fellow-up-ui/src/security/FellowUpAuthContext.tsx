import React from "react";
import {FellowUpAuthProps} from "./FellowUpAuthProvider.tsx";

export const FellowUpAuthContext = React.createContext<FellowUpAuthProps>({jwt: undefined});
