import './App.css'
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import {BottomNavBar, BottomNavBarDestination} from "./shell/BottomNavBar.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import LoupeIcon from '@mui/icons-material/Loupe';
import Overview from "./matchmaking/Overview.tsx";
import KeycloakProtected from "./security/KeycloakProtected.tsx";
import {Box, CircularProgress} from "@mui/material";
import MatchmakingMediation from "./mediation/MatchmakingMediation.tsx";

export default function App() {

  const bottomNavBarDestinations: BottomNavBarDestination[] = [
    {
      path: '/matchmaking',
      text: 'Matchmaking',
      icon: <LoupeIcon/>
    }
  ]

  return (
    <KeycloakProtected loadingComponent={
      <Box sx={{display: 'flex', width: '100%', height: '90dvh', justifyContent: 'center', alignItems: 'center'}}>
        <CircularProgress/>
      </Box>
    }
    >
      <BrowserRouter>
        <Routes>
          <Route path="/matchmaking" element={<Overview/>}/>
          <Route path="/matchmaking/:matchmakingId/mediation" element={<MatchmakingMediation/>}/>
        </Routes>
        <BottomNavBar destinations={bottomNavBarDestinations}/>
      </BrowserRouter>
    </KeycloakProtected>

  )
}
