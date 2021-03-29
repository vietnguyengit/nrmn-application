import React from 'react';
import {PropTypes} from 'prop-types';
import {Box, Grid} from '@material-ui/core';

const ObservableItemTemplate = ({properties, title}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Box component="div" width={600}>
        <h1>{title}</h1>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['observableItemName']}
          </Grid>
          <Grid item xs={6}>
            {el['obsItemTypeId']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['commonName']}
          </Grid>
          <Grid item xs={6}>
            {el['phylum']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['class']}
          </Grid>
          <Grid item xs={6}>
            {el['order']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['family']}
          </Grid>
          <Grid item xs={6}>
            {el['genus']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['speciesEpithet']}
          </Grid>
          <Grid item xs={6}>
            {el['letterCode']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['reportGroup']}
          </Grid>
          <Grid item xs={6}>
            {el['habitatGroups']}
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

ObservableItemTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemTemplate;