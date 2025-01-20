import {LocalizationProvider, PickerValidDate, StaticTimePicker} from "@mui/x-date-pickers";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFnsV3";
import {set} from "date-fns";

export interface TimeSelectProps {
  selectedDate?: Date;
  onTimeSelect: (time: Date) => void;
}

export function TimeSelect(props: TimeSelectProps) {
  const startTime = set(props.selectedDate ?? new Date(), {hours: 18, minutes: 0});
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <StaticTimePicker value={startTime} onAccept={
        (value: PickerValidDate | null) => {
          if (value) {
            props.onTimeSelect(value);
          }
        }
      }/>
    </LocalizationProvider>
  );
}
