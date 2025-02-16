import {DataGrid, GridColDef} from "@mui/x-data-grid";
import Matchmaking from "./Matchmaking.ts";

const columns: GridColDef<(Matchmaking[])[number]>[] = [
  {field: 'category', headerName: 'Category', flex: 1},
  {field: 'at', headerName: 'Time', flex: 2, headerAlign: "right", align: "right", type: 'dateTime'},
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