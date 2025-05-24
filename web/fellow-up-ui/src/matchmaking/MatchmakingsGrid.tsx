import {DataGrid, GridColDef} from "@mui/x-data-grid";
import Matchmaking from "./Matchmaking.ts";
import {ReactNode} from "react";
import {Chip} from "@mui/material";

const columns: GridColDef<(Matchmaking[])[number]>[] = [
  {
    field: 'category',
    headerName: 'Category',
    flex: 1
  },
  {
    field: 'status',
    renderCell: (params) => renderStatusCell(params.row),
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

function renderStatusCell(matchmaking: Matchmaking): ReactNode {
  let status: string = "Still looking";
  let color: 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' = "primary";
  if (matchmaking?.mediationId) {
    status = 'Mediating';
    color = 'success';
  }
  if (status) {
    return (
      <Chip label={status} color={color} variant="outlined"></Chip>
    );
  }
}
