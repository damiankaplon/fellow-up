import {IconButton, Typography} from "@mui/material";
import AddBoxIcon from '@mui/icons-material/AddBox';

export default function Matches() {
  return (
      <div style={{display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
        <Typography variant={"h6"}>
          Matches
        </Typography>
        <IconButton>
          <AddBoxIcon fontSize="large" color="primary"/>
        </IconButton>
      </div>
  );
}