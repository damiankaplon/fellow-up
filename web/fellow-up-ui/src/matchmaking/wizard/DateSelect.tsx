import {DateCalendar, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFnsV3";

export interface DateSelectProps {
    onDateSelect: (date: Date) => void;
}

export function DateSelect(props: DateSelectProps) {
    return (
        <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DateCalendar onChange={(value: Date) => props.onDateSelect(value)}/>
        </LocalizationProvider>
    );
}