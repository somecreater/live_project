import { useState } from 'react'
import { Routes, Route } from 'react-router-dom';
import HomePage from './common/page/HomePage';
import LoginPage from './user/page/LoginPage';
import SignPage from './user/page/SignPage';
import MyPage from './user/page/MyPage';

function App() {

  return (
    <>
      <div className="app-wrapper">
        <main className='main-content'>
          <Routes>
            <Route path="/" element={<HomePage/>}/>
            <Route path="/user/login" element={<LoginPage />}/>
            <Route path="/user/sign" element={<SignPage/>} />
            <Route path="/user/mypage" element={<MyPage/>} />
          </Routes>
        </main>
      </div>
    </>
  )
}

export default App
