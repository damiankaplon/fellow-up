import {IconButton, TextField} from "@mui/material";
import React, {ChangeEvent} from "react";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

export interface CategorySelectProps {
  onCategorySelect: (category: string) => void;
}

const inputValidator = (input: string) => input.length > 0;

export function CategorySelect(props: CategorySelectProps) {
  const [category, setCategory] = React.useState('');
  const [isValid, setIsValid] = React.useState(true);
  const confirmButton = (
    <IconButton onClick={() => {
      if (isValid) {
        props.onCategorySelect(category);
      }
    }}>
      <NavigateNextIcon/>
    </IconButton>);
  return (
    <TextField
      error={!isValid}
      helperText={isValid ? '' : 'Category must not be empty'}
      label="Category"
      onChange={(event: ChangeEvent<HTMLInputElement>) => {
        setCategory(event.target.value);
        const isValid = inputValidator(event.target.value);
        setIsValid(isValid);
      }}
      variant="standard"
      slotProps={{input: {endAdornment: confirmButton}}}
    />
  );

}