// 나 장수민이오.

import { Route, Routes } from 'react-router-dom';
import { ThemeProvider, createGlobalStyle } from 'styled-components';

import Home from './pages/Home';
import Login from './pages/Login';
import RegisterFunnel from './pages/registerFunnel/RegisterFunnel';
import FilterFunnel from './pages/filterFunnel/FilterFunnel';
import MatchOption from './pages/MatchOption';
import NotFound from './pages/NotFound';
import StartMatch from './pages/StartMatch';
import Notify from './pages/notify/Notify';
import SuggestByZipsa from './pages/notify/SuggestByZipsa';
import SuggestByUser from './pages/notify/SuggestByUser';
import ExperimentCommonComponent from './pages/ExperimentCommonComponent';
import Map from './pages/Map';
import UserRoomList from './pages/createRoomFunnel/UserRoomList';
import UserRoomDetail from './pages/createRoomFunnel/UserRoomDetail';
import CreateRoomFunnel from './pages/createRoomFunnel/CreateRoomFunnel';
import CompletedCreationRoom from './pages/createRoomFunnel/CompletedCreationRoom';
import ZipsaRoomList from './pages/zipsaRoom/ZipsaRoomList';
import ZipsaRoomDetail from './pages/zipsaRoom/ZipsaRoomDetail';

const GlobalStyle = createGlobalStyle`
	*,
	*::before,
	*::after {
		box-sizing: border-box;
	}
	*::placeholder {
    font-family: 'NotoSansKR', sans-serif;
    font-weight: 200;
  }
	html,
	body,
	div,
	span,
	applet,
	object,
	iframe,
	h1,
	h2,
	h3,
	h4,
	h5,
	h6,
	p,
	blockquote,
	pre,
	a,
	abbr,
	acronym,
	address,
	big,
	cite,
	code,
	del,
	dfn,
	em,
	img,
	ins,
	kbd,
	q,
	s,
	samp,
	small,
	strike,
	strong,
	sub,
	sup,
	tt,
	var,
	b,
	u,
	i,
	center,
	dl,
	dt,
	dd,
	ol,
	ul,
	li,
	fieldset,
	form,
	label,
	legend,
	table,
	caption,
	tbody,
	tfoot,
	thead,
	tr,
	th,
	td,
	article,
	aside,
	canvas,
	details,
	embed,
	figure,
	figcaption,
	footer,
	header,
	hgroup,
	menu,
	nav,
	output,
	ruby,
	section,
	summary,
	time,
	mark,
	audio,
	video,
	button,
	input,
	textarea {
		margin: 0;
		padding: 0;
		border: 0;
		font-size: 100%;
		font: inherit;
		font-family: 'NotoSansKR', sans-serif;
		vertical-align: baseline;
	}
	article,
	aside,
	details,
	figcaption,
	figure,
	footer,
	header,
	hgroup,
	menu,
	nav,
	button,
	textarea,
	section {
		display: block;
	}
	body {
		line-height: 1;
	}
	ol,
	ul {
		list-style: none;
	}
	blockquote,
	q {
		quotes: none;
	}
	blockquote:before,
	blockquote:after,
	q:before,
	q:after {
		content: '';
		content: none;
	}
	table {
		border-collapse: collapse;
		border-spacing: 0;
	}
`;

const Theme = {
  colors: {
    primary: '#f5f5f5',
    secondary: '#ffffff',
  },
};

function App() {
  return (
    <ThemeProvider theme={Theme}>
      <GlobalStyle />
      <Routes>
        <Route index element={<Home />}></Route>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/register" element={<RegisterFunnel />}></Route>
        <Route path="/filter" element={<FilterFunnel />}></Route>
        <Route path="/matchOption" element={<MatchOption />}></Route>

        <Route path="/startMatch" element={<StartMatch />}></Route>
        <Route path="/notify" element={<Notify />}></Route>
        <Route path="/suggest-by-zipsa" element={<SuggestByZipsa />}></Route>
        <Route
          path="/suggest-by-user/:notificationId"
          element={<SuggestByUser />}
        ></Route>

        <Route
          path="/experiment"
          element={<ExperimentCommonComponent />}
        ></Route>
        <Route path="/map" element={<Map />}></Route>
        <Route path="*" element={<NotFound />}></Route>

        <Route path="/rooms" element={<UserRoomList />}></Route>
        <Route path="/rooms/create" element={<CreateRoomFunnel />}></Route>
        <Route
          path="/rooms/detail/:roomId"
          element={<UserRoomDetail />}
        ></Route>
        <Route path="/rooms/zipsa" element={<ZipsaRoomList />}></Route>
        <Route
          path="/rooms/zipsa/detail/:roomId"
          element={<ZipsaRoomDetail />}
        ></Route>
        <Route
          path="/rooms/complete"
          element={<CompletedCreationRoom />}
        ></Route>
      </Routes>
    </ThemeProvider>
  );
}

export default App;
