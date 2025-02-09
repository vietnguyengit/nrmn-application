import React, {useState} from 'react';
import {PropTypes} from 'prop-types';
import {Search as SearchIcon, FindReplace as FindReplaceIcon} from '@mui/icons-material/';
import {Box, Button, Checkbox, FormControlLabel, TextField} from '@mui/material';

const clone = (obj) => {
  var obj1 = {};
  var obj2 = {};
  Object.keys(obj).forEach(function (key) {
    obj1[key] = obj[key];
    obj2[key] = obj[key];
  });
  return [obj2, obj1];
};

const stringCompare = (source, target, matchCase, matchWholeString) => {
  if (typeof source !== 'string' || typeof target !== 'string' || source.length === 0 || target.length === 0) return -1;
  if (matchWholeString) return source.normalize() === target.normalize() ? 0 : -1;
  if (matchCase) return source.indexOf(target);
  else return source.toUpperCase().indexOf(target.toUpperCase());
};

const FindReplacePanel = (props) => {
  const [matchCase, setMatchCase] = useState(false);
  const [matchColumn, setMatchColumn] = useState(false);
  const [matchCell, setMatchCell] = useState(false);
  const [inProgress, setInProgress] = useState(false);
  const [replaceAll, setReplaceAll] = useState(false);

  const [status, setStatus] = useState('');
  const [findString, setFindString] = useState('');
  const [replaceString, setReplaceString] = useState('');
  const [currentFindString, setCurrentFindString] = useState('');

  const [initialPosition, setInitialPosition] = useState({});

  // where all grid data and metadata is stored
  const context = props.api.gridOptionsWrapper.gridOptions.context;

  const reset = () => {
    setInProgress(false);
    setStatus('');
    setFindString('');
    setReplaceString('');
    setCurrentFindString('');
    context.findResults = [];
    context.highlighted = [];
    props.api.redrawRows();
    if (initialPosition) {
      props.api.setFocusedCell(initialPosition.rowIndex, initialPosition.column);
      props.api.ensureIndexVisible(initialPosition.rowIndex);
      props.api.ensureColumnVisible(initialPosition.column);
      setInitialPosition({});
    }
  };

  const highlightNextResult = () => {
    if (context.findResults.length < 1) return;
    const result = context.findResults.shift();
    props.api.ensureIndexVisible(result.row);
    props.api.ensureColumnVisible(result.col);
    props.api.setFocusedCell(result.row, result.col);
    context.findResults.push(result);
  };

  const findInGrid = (findString, matchCase) => {
    const focusedCell = props.api.getFocusedCell();
    const selectedRanges = props.api.getCellRanges();
    const selectedColumns = selectedRanges.map((c) => c.columns[0].colId);
    if (focusedCell) setInitialPosition({rowIndex: focusedCell.rowIndex, column: focusedCell.column});
    context.findResults = [];
    context.highlighted = [];
    props.api.forEachNodeAfterFilterAndSort((node) => {
      for (let columnIdx in props.api.columnModel.columnDefs) {
        const columnDef = props.api.columnModel.columnDefs[columnIdx];
        const fieldValue = node.data[columnDef.field];
        if ((matchColumn && !selectedColumns.includes(columnDef.field)) || !fieldValue || columnDef.editable === false) continue;
        const idx = stringCompare(fieldValue.toString(), findString, matchCase, matchCell);
        if (idx >= 0) {
          context.highlighted[node.rowIndex] = context.highlighted[node.rowIndex] || [];
          context.highlighted[node.rowIndex][columnDef.field] = true;
          context.findResults.push({row: node.rowIndex, col: columnDef.field});
        }
      }
    });
    props.api.redrawRows();
    const count = context.findResults.length;
    setCurrentFindString(count > 0 ? findString : '');
    setStatus(`Found ${count} matches`);
  };

  const onFind = () => {
    setInProgress(true);
    if (findString !== currentFindString) findInGrid(findString, matchCase);
    highlightNextResult();
  };

  const escapeRegExp = (string) => {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  };

  const onReplace = () => {
    const focusedCell = props.api.getFocusedCell();
    const agRow = props.api.getDisplayedRowAtIndex(focusedCell.rowIndex);
    let [oldRow, newRow] = clone(agRow.data);
    const colId = focusedCell.column.colId;
    const findRegex = new RegExp(escapeRegExp(findString), matchCase ? '' : 'i');
    newRow[colId] = matchCell ? replaceString : oldRow[colId].replace(findRegex, replaceString);
    context.pushUndo(props.api, [oldRow]);
    const rowDataIndex = context.rowData.findIndex((r) => r.id === agRow.data.id);
    context.rowData[rowDataIndex] = newRow;
    props.api.setRowData(context.rowData);
    highlightNextResult();
  };

  const onReplaceAll = () => {
    let undo = [];
    const selectedColumns = props.api.getCellRanges().map((c) => c.columns[0].colId);
    for (const rowIndex in context.rowData) {
      const row = context.rowData[rowIndex];
      let [oldRow, newRow] = clone(row);
      let modifiedRow = false;
      Object.keys(row).forEach(function (fieldKey) {
        if (['id', 'pos'].includes(fieldKey)) return;
        const fieldValue = row[fieldKey];
        if ((matchColumn && !selectedColumns.includes(fieldKey)) || !fieldValue) return;
        const substringIdx = stringCompare(fieldValue.toString(), findString, matchCase, matchCell);
        if (substringIdx >= 0) {
          const findRegex = new RegExp(escapeRegExp(findString), matchCase ? '' : 'i');
          newRow[fieldKey] = matchCell ? replaceString : oldRow[fieldKey].replace(findRegex, replaceString);
          modifiedRow = true;
        }
      });
      if (modifiedRow) {
        undo.push(oldRow);
        context.rowData[rowIndex] = newRow;
      }
    }
    context.pushUndo(props.api, undo);
    props.api.setRowData(context.rowData);
    props.api.redrawRows();
  };

  const onKeyDown = (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'z' && e.type === 'keydown')
      props.agGridReact.props.defaultColDef.suppressKeyboardEvent({event: e, api: props.api});
  };

  return (
    <Box m={2} mr={4}>
      <Button variant="outlined" onClick={reset}>
        Reset
      </Button>
      <TextField
        size="small"
        placeholder="Find.."
        autoFocus={false}
        sx={{marginTop: 2, width: '100%'}}
        value={findString}
        onKeyDown={onKeyDown}
        onChange={(e) => setFindString(e.target.value)}
      />
      <Button variant="outlined" sx={{marginTop: 2, width: '100%'}} startIcon={<SearchIcon />} onClick={onFind}>
        Find
      </Button>
      <TextField
        size="small"
        placeholder="Replace With.."
        sx={{marginTop: 2, width: '100%'}}
        value={replaceString}
        autoFocus={false}
        onKeyDown={onKeyDown}
        onChange={(e) => setReplaceString(e.target.value)}
      />
      <Button
        variant="outlined"
        sx={{marginTop: 2, width: '100%'}}
        startIcon={<FindReplaceIcon />}
        onKeyDown={onKeyDown}
        onClick={replaceAll ? onReplaceAll : onReplace}
        disabled={currentFindString.length < 1}
      >
        Replace
      </Button>
      <FormControlLabel
        label="Match case"
        sx={{marginTop: 2, width: '100%'}}
        control={<Checkbox disabled={inProgress} checked={matchCase} onChange={(e) => setMatchCase(e.target.checked)} />}
      />
      <FormControlLabel
        label="Only this column"
        sx={{width: '100%'}}
        control={<Checkbox disabled={inProgress} checked={matchColumn} onChange={(e) => setMatchColumn(e.target.checked)} />}
      />
      <FormControlLabel
        label="Match whole cell"
        sx={{width: '100%'}}
        control={<Checkbox disabled={inProgress} checked={matchCell} onChange={(e) => setMatchCell(e.target.checked)} />}
      />
      <FormControlLabel
        label="Replace All"
        sx={{width: '100%'}}
        control={<Checkbox checked={replaceAll} onChange={(e) => setReplaceAll(e.target.checked)} />}
      />
      <Box sx={{marginTop: 2, width: '100%'}}>{status}</Box>
    </Box>
  );
};

FindReplacePanel.propTypes = {
  api: PropTypes.any,
  agGridReact: PropTypes.any
};

export default FindReplacePanel;
