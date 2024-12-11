import {CardContent, CardHeader, IconButton, Paper} from "@mui/material";
import AddBoxIcon from '@mui/icons-material/AddBox';

export default function Matchmakings() {
  return (
      <Paper>
        <CardContent>
          <CardHeader title={"Matchmakings"}
                      action={<IconButton>
                        <AddBoxIcon fontSize="large" color="primary"/>
                      </IconButton>}/>
        </CardContent>
      </Paper>
  );
}