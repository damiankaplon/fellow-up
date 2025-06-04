import {DataGrid, GridColDef} from "@mui/x-data-grid";
import Matchmaking from "./Matchmaking.ts";
import {Chip} from "@mui/material";
import {useNavigate} from "react-router-dom";

const columns: GridColDef<(Matchmaking[])[number]>[] = [
  {
    field: 'category',
    headerName: 'Category',
    flex: 1
  },
  {
    field: 'status',
    renderCell: (params) => StatusCell(params.row),
    headerName: 'Status',
    headerAlign: 'center',
    align: 'center',
    flex: 1
  },
  {
    field: 'at',
    headerName: 'Time',
    flex: 1,
    headerAlign: 'right',
    align: 'right',
    type: 'dateTime'
  },
];

export default function MatchmakingsGrid({matchmakings}: { matchmakings: Matchmaking[] }) {
  return (
    <DataGrid style={{width: '100%'}}
              columns={columns}
              rows={matchmakings}
              pageSizeOptions={[5]}
    />
  );
}

function StatusCell(matchmaking: Matchmaking) {
  const navigate = useNavigate();
  let color: 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' = "primary";
  let text: string | undefined;
  if (matchmaking?.status === 'MEDIATING') {
    color = 'success';
    text = 'Mediating';
  } else {
    color = 'primary';
    text = 'Still looking';
  }
  return (
    <Chip
      label={text}
      color={color}
      variant="outlined"
      onClick={() => navigate(`/matchmaking/${matchmaking.id}/mediation`)}
    />
  );
}
