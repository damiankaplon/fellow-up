import {Box, Button, Checkbox, IconButton, List, ListItem, Paper, Typography} from '@mui/material';
import {ArrowBack, ArrowForward} from '@mui/icons-material';
import {APIProvider, Map, Marker} from '@vis.gl/react-google-maps';
import {type Proposal} from "./Proposal.ts";
import {type Mediation} from "./Mediation.ts";
import {useState} from "react";
import {Fellow} from "./Fellow.ts";

export type MediationProps = {
  mediation: Mediation;
  googleMapsApiKey: string;
  onAcceptProposal: (proposal: Proposal) => void;
  onMapError?: (error: Error) => void;
}

export default function MediationComponent(props: MediationProps) {
  const [currentProposalIndex, setCurrentProposalIndex] = useState(0);

  if (!props.mediation || props.mediation.proposals.length === 0) {
    return <Typography>No mediation data available.</Typography>;
  }

  const {category, proposals} = props.mediation;

  if (!proposals || proposals.length === 0) {
    return (
      <Box sx={{padding: 2, textAlign: 'center'}}>
        <Typography variant="h5" gutterBottom>
          {category}
        </Typography>
        <Typography>No mediation proposals available for this category.</Typography>
      </Box>
    );
  }

  const currentProposal: Proposal = proposals[currentProposalIndex];

  const handleNextProposal: () => void = () => {
    setCurrentProposalIndex((prevIndex) => (prevIndex + 1) % proposals.length);
  };

  const handlePrevProposal: () => void = () => {
    setCurrentProposalIndex((prevIndex) => (prevIndex - 1 + proposals.length) % proposals.length);
  };

  const handleAccept: () => void = () => {
    props.onAcceptProposal(currentProposal);
  };

  return (
    <div style={{display: "flex", height: '80vh', width: '100%', flexDirection: 'column', justifyContent: 'center'}}>
      <Typography variant="h4" gutterBottom textAlign="center" color="primary">
        {category}
      </Typography>

      <Box sx={{display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column'}}>
        <Box sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          width: '100%',
        }}>
          <IconButton onClick={handlePrevProposal} disabled={proposals.length <= 1} aria-label="previous proposal">
            <ArrowBack/>
          </IconButton>
          <Typography variant="h6">
            Proposal {currentProposalIndex + 1} of {proposals.length}
          </Typography>
          <IconButton onClick={handleNextProposal} disabled={proposals.length <= 1} aria-label="next proposal">
            <ArrowForward/>
          </IconButton>
        </Box>

        <Paper elevation={2}
               sx={{margin: 1, width: '95%', display: 'flex', flexDirection: 'column', justifyContent: 'center'}}>
          {props.googleMapsApiKey ? (
            <Box sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              height: '30vh',
              width: '100%'/* Consistent height */
            }}>
              <APIProvider
                apiKey={props.googleMapsApiKey}
                onLoad={() => console.log("Google Maps API Loaded for Mediation proposal.")}
                onError={(e) => {
                  console.error("Google Maps API Error for Mediation proposal:", e);
                  if (props.onMapError) props.onMapError(new Error("Failed to load Google Maps"));
                }}
              >
                <Map
                  style={{height: '98%', width: '98%'}}
                  center={currentProposal.location}
                  zoom={15}
                  key={currentProposalIndex}
                  fullscreenControl={true}
                  gestureHandling={'greedy'}
                >
                  <Marker position={{lat: currentProposal.location.lat, lng: currentProposal.location.lng}}/>
                </Map>
              </APIProvider>
            </Box>
          ) : (
            <Typography color="error" sx={{marginY: 2}}>
              Map cannot be displayed.
            </Typography>
          )}

          <List sx={{width: '100%'}}>
            {
              props.mediation.fellows.map((fellow: Fellow) => (
                <ListItem key={fellow.id}>
                  <Checkbox
                    disabled={true}
                    checked={currentProposal.acceptedBy.some((acceptedBy: Fellow) => acceptedBy.id === fellow.id)}>
                  </Checkbox>
                  <Typography>{fellow.name}</Typography>
                </ListItem>
              ))
            }
          </List>

          <Button variant="contained" color="success" onClick={handleAccept} sx={{padding: 1}}>
            Accept This Proposal
          </Button>
        </Paper>
      </Box>
    </div>
  );
}
