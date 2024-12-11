import {BottomNavigation, BottomNavigationAction, Paper} from "@mui/material";
import {ReactNode, SyntheticEvent, useState} from "react";
import {useNavigate} from "react-router-dom";

export interface BottomNavBarDestination {
  path: string;
  text?: string;
  icon?: ReactNode
}

export function BottomNavBar({destinations}: { destinations: BottomNavBarDestination[] }) {
  const [selectedDestination, setDestination] = useState(0);
  const navigate = useNavigate();
  const onChange = (_: SyntheticEvent, newValue: number) => {
    setDestination(newValue);
    navigate(destinations[newValue].path);
  };
  return (
      <Paper sx={{position: 'fixed', bottom: 0, left: 0, right: 0}} elevation={3}>
        <BottomNavigation showLabels={false} value={selectedDestination} onChange={onChange}>
          {destinations.map((destination, index) => (
              <BottomNavigationAction key={index} label={destination.text} icon={destination.icon}/>
          ))}
        </BottomNavigation>
      </Paper>
  )
}
