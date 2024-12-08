import {BottomNavigation, BottomNavigationAction, Paper} from "@mui/material";
import {Link} from "react-router-dom";
import {ReactNode, SyntheticEvent, useState} from "react";

export interface BottomNavBarDestination {
  path: string;
  text?: string;
  icon?: ReactNode
}

export function BottomNavBar({destinations}: { destinations: BottomNavBarDestination[] }) {
  const [selectedDestination, setDestination] = useState(0)
  const onChange = (_: SyntheticEvent, newValue: number) =>
      setDestination(newValue);
  return (
      <Paper sx={{position: 'fixed', bottom: 0, left: 0, right: 0}} elevation={3}>

        <BottomNavigation value={selectedDestination} onChange={onChange}>
          {destinations.map((destination) => (
              <BottomNavigationAction label={destination.text} icon={destination.icon}>
                <Link to={destination.path}/>
              </BottomNavigationAction>
          ))}
        </BottomNavigation>

      </Paper>
  )
}
