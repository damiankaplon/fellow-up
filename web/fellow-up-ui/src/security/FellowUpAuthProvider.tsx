import React from "react";
import {FellowUpAuthContext} from "./FellowUpAuthContext.tsx";


export interface FellowUpAuthProps {
  jwt?: string;
}

export default function FellowUpAuthProvider(props: { jwtProvider: () => string, children: React.ReactNode}) {
  return (
    <FellowUpAuthContext.Provider value={{jwt: props.jwtProvider()}}>
      {props.children}
    </FellowUpAuthContext.Provider>
  );
}


