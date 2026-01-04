import { useState } from 'react'
import { Routes, Route } from 'react-router-dom';
import HomePage from './common/page/HomePage';
import LoginPage from './user/page/LoginPage';
import SignPage from './user/page/SignPage';
import MyPage from './user/page/MyPage';
import MySubscriptionPage from './user/page/MySubscriptionPage';
import ManagerPage from './user/page/ManagerPage';
import UpdateUserPage from './user/page/UpdateUserPage';
import ChannelPage from './channel/page/ChannelPage';
import MyChannelPage from './channel/page/MyChannelPage';
import ChannelDetailPage from './channel/page/ChannelDetailPage';

import { BrowserRouter } from 'react-router-dom'
import AppLayout from './AppLayout';
import "./App.css"


function App() {

  return (
    <>
      <BrowserRouter>
        <AppLayout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/user/login" element={<LoginPage />} />
            <Route path="/user/sign" element={<SignPage />} />
            <Route path="/user/mypage" element={<MyPage />} />
            <Route path="/user/subscriptions" element={<MySubscriptionPage />} />
            <Route path="/user/update" element={<UpdateUserPage />} />
            <Route path="/manager" element={<ManagerPage />} />

            <Route path="/channel" element={<ChannelPage />} />
            <Route path="/channel/:id" element={<ChannelDetailPage />} />
            <Route path="/channel/my_channel" element={<MyChannelPage />} />
          </Routes>
        </AppLayout>
      </BrowserRouter>
    </>
  )
}

export default App
