import { useState } from 'react'
import { Routes, Route } from 'react-router-dom';
import HomePage from './common/page/HomePage';
import LoginPage from './user/page/LoginPage';
import SignPage from './user/page/SignPage';
import MyPage from './user/page/MyPage';
import ManagerPage from './user/page/ManagerPage';
import UpdateUserPage from './user/page/UpdateUserPage';
import TopMenu from './common/component/TopMenu';
import Footer from './common/component/Footer';
import SideMenu from './common/component/SideMenu';

function App() {

  return (
    <>
    <TopMenu/>
      <div className="app-wrapper">
        <main className='main-content'>
          <SideMenu/>
          <Routes>
            <Route path="/" element={<HomePage/>}/>
            <Route path="/user/login" element={<LoginPage />} />
            <Route path="/user/sign" element={<SignPage/>} />
            <Route path="/user/mypage" element={<MyPage/>} />
            <Route path="/user/update" element={<UpdateUserPage/>} />
            <Route path="/manager" element={<ManagerPage/>} />
          </Routes>
        </main>
      </div>
      <Footer/>
    </>
  )
}

export default App
