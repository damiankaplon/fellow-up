import './App.css'
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import {BottomNavBar, BottomNavBarDestination} from "./shell/BottomNavBar.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import LoupeIcon from '@mui/icons-material/Loupe';
import Overview from "./matchmaking/Overview.tsx";

export default function App() {

  const bottomNavBarDestinations: BottomNavBarDestination[] = [
    {
      path: '/matchmaking',
      text: 'Matchmaking',
      icon: <LoupeIcon/>
    }
  ]

  return (
      <BrowserRouter>
        <Routes>
          <Route path="/matchmaking" element={<Overview/>}/>
        </Routes>
        <BottomNavBar destinations={bottomNavBarDestinations}/>
      </BrowserRouter>
  )
}
