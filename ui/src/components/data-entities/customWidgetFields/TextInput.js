import React from 'react';
import {useDispatch, useSelector} from 'react-redux';

import {TextField, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import {setField} from '../middleware/entities';

const TextInput = ({name, schema, uiSchema}) => {
  const dispatch = useDispatch();
  const value = useSelector((state) => state.form.data[name]);
  const error = useSelector((state) => state.form.errors).find((e) => e.property === name);
  const readOnly = uiSchema['ui:field'] === 'readonly';

  return (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      {readOnly ? (
        <Typography>{value}</Typography>
      ) : (
        <TextField
          color="primary"
          inputProps={{
            readOnly: readOnly
          }}
          error={error}
          helperText={error?.message}
          value={value}
          onChange={(event) => {
            const newValue = event.target.value;
            dispatch(setField({newValue, entity: name}));
          }}
        />
      )}
    </>
  );
};

TextInput.propTypes = {
  name: PropTypes.string,
  title: PropTypes.string,
  schema: PropTypes.object,
  uiSchema: PropTypes.object,
  formData: PropTypes.string
};

export default TextInput;