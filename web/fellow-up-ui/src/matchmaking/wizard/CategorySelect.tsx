import {IconButton, TextField} from "@mui/material";
import React, {ChangeEvent} from "react";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

export interface CategorySelectProps {
    onCategorySelect: (category: string) => void;
}

export function CategorySelect(props: CategorySelectProps) {
    const [category, setCategory] = React.useState('');
    const confirmButton = (
        <IconButton onClick={() => props.onCategorySelect(category)}>
            <NavigateNextIcon/>
        </IconButton>);
    return (
        <TextField id="standard-basic"
                   label="Category"
                   onChange={(event: ChangeEvent<HTMLInputElement>) => setCategory(event.target.value)}
                   variant="standard"
                   slotProps={{input: {endAdornment: confirmButton}}}
        />
    );

}