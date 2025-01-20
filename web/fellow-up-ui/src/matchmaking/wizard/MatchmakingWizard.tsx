import {LinearProgress} from "@mui/material";
import {CategorySelect} from "./CategorySelect.tsx";
import React from "react";
import {DateSelect} from "./DateSelect.tsx";
import {TimeSelect} from "./TimeSelect.tsx";

interface MatchmakingWizardState {
  steps: number;
  currentStep: number;
  completed: boolean;
  category?: string;
  date?: Date;
  // This will be important at some point in time
  // location?: string;
}

export interface MatchmakingWizardResult {
  category?: string;
  date?: Date;
  // location?: string;
}

export interface MatchmakingWizardProps {
  onComplete: (userInput: MatchmakingWizardResult) => void;
}

export function MatchmakingWizard(props: MatchmakingWizardProps) {
  const [currentContent, setCurrentContent] = React.useState<React.ReactElement>();
  const [wizardState, setWizardState] = React.useState<MatchmakingWizardState>({
    steps: 3,
    currentStep: 1,
    completed: false
  });

  console.log(wizardState);

  const onCategorySelect = (category: string) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {...currentState, category: category, currentStep: currentState.currentStep + 1}
    )
  );
  const categorySelect = (<CategorySelect onCategorySelect={onCategorySelect}/>);

  const onDateSelect = (selected: Date) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {
        ...currentState,
        date: selected,
        currentStep: currentState.currentStep + 1
      }
    )
  );
  const dateSelect = (<DateSelect onDateSelect={onDateSelect}/>);

  const onTimeSelect = (value: Date) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {
        ...currentState,
        date: value,
        completed: true
      }
    )
  );
  const timeSelect = (<TimeSelect selectedDate={wizardState.date} onTimeSelect={onTimeSelect}/>);

  React.useEffect(() => {
    if (wizardState.currentStep == 1) {
      setCurrentContent(categorySelect);
    }
    if (wizardState.currentStep == 2) {
      setCurrentContent(dateSelect);
    }
    if (wizardState.currentStep == 3) {
      setCurrentContent(timeSelect);
    }
  }, [wizardState.currentStep]);

  React.useEffect(() => {
    if (wizardState.completed == true) {
      props.onComplete({
        category: wizardState.category,
        date: wizardState.date,
      });
    }
  }, [wizardState.completed]);

  return (
    <>
      <LinearProgress variant="determinate" value={wizardState.currentStep / wizardState.steps * 100}/>
      {currentContent}
    </>
  );
}
